import copy
import datetime
import os
import hashlib
import json
import pathlib
import plistlib
import subprocess
import unittest
import tempfile
from unittest.mock import patch

from configure_ios_release import configure_project, signing_identity, main

ROOT = pathlib.Path(__file__).resolve().parents[2]


class ConfigureIosReleaseTests(unittest.TestCase):
    def setUp(self):
        self.project = json.loads(subprocess.check_output([
            "plutil", "-convert", "json", "-o", "-",
            str(ROOT / "iosApp/iosApp.xcodeproj/project.pbxproj"),
        ]))

    def test_only_app_release_changes_and_xml_round_trips(self):
        before = copy.deepcopy(self.project)
        configure_project(self.project, "TESTTEAM", "Distribution profile with spaces", "1.16.2")
        changed = [key for key, value in self.project["objects"].items()
                   if before["objects"][key] != value]
        self.assertEqual(len(changed), 1)
        config = self.project["objects"][changed[0]]
        self.assertEqual(config["name"], "Release")
        settings = config["buildSettings"]
        self.assertEqual(settings["PROVISIONING_PROFILE_SPECIFIER"], "Distribution profile with spaces")
        self.assertEqual(settings["DEVELOPMENT_TEAM"], "TESTTEAM")
        self.assertEqual(settings["CODE_SIGN_STYLE"], "Manual")
        self.assertEqual(settings["CODE_SIGN_IDENTITY"], "Apple Distribution")
        self.assertEqual(settings["CURRENT_PROJECT_VERSION"], "1.16.2")
        self.assertIn("ComposeApp", settings["OTHER_LDFLAGS"])
        self.assertEqual(plistlib.loads(plistlib.dumps(self.project)), self.project)

    def test_missing_app_fails_without_mutation(self):
        for obj in self.project["objects"].values():
            if obj.get("name") == "iosApp":
                obj["name"] = "anotherApp"
        before = copy.deepcopy(self.project)
        with self.assertRaisesRegex(SystemExit, "application target"):
            configure_project(self.project, "TESTTEAM", "profile", "1.16.2")
        self.assertEqual(self.project, before)

    def test_identity_must_match_a_profile_certificate(self):
        certificate = b"test certificate bytes"
        fingerprint = hashlib.sha1(certificate).hexdigest().upper()
        profile = {"DeveloperCertificates": [certificate]}
        self.assertEqual(signing_identity(profile, f'1) {fingerprint} "Apple Distribution"'), fingerprint)
        with self.assertRaisesRegex(SystemExit, "No valid signing identity"):
            signing_identity(profile, "0 valid identities found")
        with self.assertRaisesRegex(SystemExit, "No valid signing identity"):
            signing_identity(profile, '1) ' + 'A' * 40 + ' "Other certificate"')

    def test_cli_validates_inputs_and_writes_app_settings_and_export_options(self):
        certificate = b"test certificate bytes"
        fingerprint = hashlib.sha1(certificate).hexdigest().upper()
        bundle = "com.alad1nks.dubovozki"
        profile_uuid = "11111111-2222-3333-4444-555555555555"
        profile = {
            "UUID": profile_uuid, "Name": "Distribution profile with spaces",
            "TeamIdentifier": ["TESTTEAM"], "ApplicationIdentifierPrefix": ["TESTTEAM"],
            "ExpirationDate": datetime.datetime.now() + datetime.timedelta(days=30),
            "DeveloperCertificates": [certificate],
            "Entitlements": {"application-identifier": "TESTTEAM." + bundle, "beta-reports-active": True},
        }
        real_check_output = subprocess.check_output

        def command(args, **kwargs):
            if args[0] == "security":
                self.assertEqual(args, ["security", "find-identity", "-v", "-p", "codesigning", "test-keychain"])
                return f'1) {fingerprint} "Apple Distribution"'
            return real_check_output(args, **kwargs)

        with tempfile.TemporaryDirectory() as directory:
            root = pathlib.Path(directory)
            profile_path, firebase_path = root / "profile.plist", root / "firebase.plist"
            project_path, export_path = root / "project.pbxproj", root / "export.plist"
            profile_path.write_bytes(plistlib.dumps(profile))
            firebase_path.write_bytes(plistlib.dumps({"BUNDLE_ID": bundle}))
            project_path.write_bytes((ROOT / "iosApp/iosApp.xcodeproj/project.pbxproj").read_bytes())
            args = ["configure_ios_release.py", str(profile_path), str(firebase_path), str(export_path),
                    str(project_path), "1.16.2", "test-keychain"]
            with patch("sys.argv", args), patch.dict(os.environ, {"APPLE_TEAM_ID": "TESTTEAM"}), \
                    patch("subprocess.check_output", side_effect=command), patch("builtins.print") as output:
                main()
            output.assert_called_once_with(profile_uuid)
            exported = plistlib.loads(export_path.read_bytes())
            self.assertEqual(exported["provisioningProfiles"], {bundle: profile_uuid})
            changed = plistlib.loads(project_path.read_bytes())
            configurations = [obj for obj in changed["objects"].values()
                              if obj.get("buildSettings", {}).get("CODE_SIGN_IDENTITY") == fingerprint]
            self.assertEqual(len(configurations), 1)
            self.assertEqual(configurations[0]["buildSettings"]["PROVISIONING_PROFILE_SPECIFIER"], profile["Name"])
            subprocess.run(["plutil", "-lint", str(project_path)], check=True, capture_output=True)

    def test_no_global_signing_or_linker_overrides_in_archive_command(self):
        script = (ROOT / ".github/scripts/ios-release.sh").read_text()
        archive = script.split("xcodebuild archive", 1)[1].split("xcodebuild -exportArchive", 1)[0]
        for key in ("PROVISIONING_PROFILE", "CODE_SIGN", "DEVELOPMENT_TEAM", "OTHER_LDFLAGS",
                    "FRAMEWORK_SEARCH_PATHS", "CURRENT_PROJECT_VERSION"):
            self.assertNotIn(key, archive)


if __name__ == "__main__":
    unittest.main()
