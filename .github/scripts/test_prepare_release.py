"""Exercise release allocation and commits in isolated local Git repositories, without GitHub or signing."""

import os
from pathlib import Path
import subprocess
import tempfile
import unittest

from prepare_release import ANDROID, IOS, prepare


class PrepareReleaseTest(unittest.TestCase):
    def setUp(self):
        self.original_cwd = Path.cwd()
        self.temp = tempfile.TemporaryDirectory(prefix="release-git-test-")
        self.directory = Path(self.temp.name).resolve()
        os.chdir(self.directory)
        self.addCleanup(self.cleanup)
        self.git("init", "-b", "main")
        self.git("config", "user.name", "Release test")
        self.git("config", "user.email", "release-test@example.invalid")
        self.git("config", "commit.gpgsign", "false")
        self.git("config", "core.autocrlf", "false")
        self.write_versions("2.3", 8, "1.0")
        Path("main-only.txt").write_text("main contents\n")
        self.git("add", ".")
        self.git("commit", "-m", "main baseline")
        self.main_sha = self.git("rev-parse", "HEAD")
        self.git("update-ref", "refs/remotes/origin/main", self.main_sha)

    def cleanup(self):
        os.chdir(self.original_cwd)
        # TemporaryDirectory owns this exact resolved test directory, never the real checkout.
        self.assertEqual(self.directory, Path(self.temp.name).resolve())
        self.temp.cleanup()

    def git(self, *args):
        return subprocess.check_output(["git", *args], text=True, encoding="utf-8", stderr=subprocess.PIPE).strip()

    def write_versions(self, version, code, ios=None):
        Path(ANDROID).parent.mkdir(parents=True, exist_ok=True)
        Path(IOS).parent.mkdir(parents=True, exist_ok=True)
        Path(ANDROID).write_text(
            f'android {{\n    defaultConfig {{\n        versionCode = {code}\n'
            f'        versionName = "{version}"\n    }}\n}}\n', encoding="utf-8")
        Path(IOS).write_text(f"TEAM_ID=\nCURRENT_PROJECT_VERSION=1\nMARKETING_VERSION={ios or version}\n",
                             encoding="utf-8")

    def release(self, version, code):
        self.git("switch", "-C", "fixture", self.main_sha)
        self.write_versions(version, code)
        Path("release-only.txt").write_text("must not carry into the next release\n")
        self.git("add", ".")
        self.git("commit", "-m", f"release {version}")
        self.git("update-ref", f"refs/remotes/origin/release/{version}", self.git("rev-parse", "HEAD"))
        self.git("switch", "main")

    def test_release_is_one_version_commit_on_main_for_both_platforms(self):
        self.release("2.3", 8)
        self.assertEqual(prepare("100"), ("release/2.4", "2.4", True))
        self.assertEqual(self.git("rev-parse", "HEAD^"), self.main_sha)
        self.assertEqual(self.git("rev-parse", "main"), self.main_sha)
        self.assertEqual(set(self.git("diff", "--name-only", "HEAD^", "HEAD").splitlines()), {ANDROID, IOS})
        self.assertIn('versionName = "2.4"', Path(ANDROID).read_text())
        self.assertIn("versionCode = 9", Path(ANDROID).read_text())
        self.assertIn("MARKETING_VERSION=2.4", Path(IOS).read_text())
        self.assertIn("CURRENT_PROJECT_VERSION=1", Path(IOS).read_text())
        self.assertFalse(Path("release-only.txt").exists())

    def test_versions_are_sorted_numerically(self):
        self.release("2.9", 10)
        self.release("2.10", 11)
        self.assertEqual(prepare("101")[1], "2.11")

    def test_major_version_takes_precedence(self):
        self.release("2.99", 10)
        self.release("3.0", 11)
        self.assertEqual(prepare("102")[1], "3.1")

    def test_version_code_exceeds_main_and_every_release(self):
        self.release("2.2", 25)
        self.release("2.3", 10)
        prepare("103")
        self.assertIn("versionCode = 26", Path(ANDROID).read_text())

    def test_no_release_branches_uses_main_version(self):
        self.assertEqual(prepare("104")[1], "2.4")

    def test_non_numeric_and_nested_release_branches_are_ignored(self):
        for suffix in ("next", "ios/99.0", "2.99-beta", "02.99", "2.99.0"):
            self.git("update-ref", f"refs/remotes/origin/release/{suffix}", self.main_sha)
        self.assertEqual(prepare("105")[1], "2.4")

    def test_retry_reuses_the_published_commit(self):
        branch, _, _ = prepare("106")
        sha = self.git("rev-parse", "HEAD")
        self.git("update-ref", f"refs/remotes/origin/{branch}", sha)
        self.git("switch", "main")
        self.assertEqual(prepare("106"), (branch, "2.4", False))
        self.assertEqual(self.git("rev-parse", f"refs/remotes/origin/{branch}"), sha)
        # A different run allocates the next release, despite main's unchanged versionCode.
        self.assertEqual(prepare("107")[1], "2.5")
        self.assertIn("versionCode = 10", Path(ANDROID).read_text())

    def test_retry_refuses_to_overwrite_an_advanced_release(self):
        branch, _, _ = prepare("108")
        Path("hotfix.txt").write_text("hotfix\n")
        self.git("add", ".")
        self.git("commit", "-m", "hotfix")
        self.git("update-ref", f"refs/remotes/origin/{branch}", self.git("rev-parse", "HEAD"))
        self.git("switch", "main")
        with self.assertRaisesRegex(ValueError, "advanced after creation"):
            prepare("108")

    def test_downgrade_is_rejected_before_mutation(self):
        self.release("1.9", 7)
        with self.assertRaisesRegex(ValueError, "below main"):
            prepare("109")
        self.assertEqual(self.git("rev-parse", "HEAD"), self.main_sha)
        self.assertEqual(self.git("status", "--porcelain"), "")

    def test_dirty_checkout_is_rejected(self):
        Path("uncommitted.txt").write_text("user changes\n")
        with self.assertRaisesRegex(ValueError, "checkout must be clean"):
            prepare("110")

    def test_push_creates_only_an_absent_remote_branch(self):
        branch, _, _ = prepare("111")
        head = self.git("rev-parse", "HEAD")
        for already_exists in (False, True):
            with self.subTest(already_exists=already_exists):
                remote = str(self.directory / f"remote-{already_exists}.git")
                self.git("init", "--bare", remote)
                if already_exists:
                    # Even an existing ref that could be fast-forwarded must be left untouched.
                    self.git("push", remote, f"main:refs/heads/{branch}")
                result = subprocess.run(
                    ["git", "push", f"--force-with-lease=refs/heads/{branch}:", remote,
                     f"HEAD:refs/heads/{branch}"], capture_output=True, text=True)
                self.assertEqual(result.returncode == 0, not already_exists, result.stderr)
                actual = self.git("ls-remote", remote, f"refs/heads/{branch}").split()[0]
                self.assertEqual(actual, self.main_sha if already_exists else head)


if __name__ == "__main__":
    unittest.main()
