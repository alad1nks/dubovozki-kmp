#!/usr/bin/env bash
# Run from the repository root on an ephemeral GitHub-hosted macOS runner.
set -euo pipefail

required_secrets=(
  FIREBASE_CONFIG_IOS
  IOS_DISTRIBUTION_CERTIFICATE_BASE64
  IOS_DISTRIBUTION_CERTIFICATE_PASSWORD
  IOS_PROVISIONING_PROFILE_BASE64
  APPLE_TEAM_ID
  APP_STORE_CONNECT_API_KEY_ID
  APP_STORE_CONNECT_API_ISSUER_ID
  APP_STORE_CONNECT_API_KEY_BASE64
)
for name in "${required_secrets[@]}"; do
  if [[ -z "${!name:-}" ]]; then
    echo "::error::Missing GitHub Actions secret: $name"
    exit 1
  fi
done

: "${RUNNER_TEMP:?This script requires a GitHub-hosted macOS runner}"
: "${GITHUB_RUN_NUMBER:?Missing workflow run number}"
: "${GITHUB_RUN_ATTEMPT:?Missing workflow run attempt}"

# CFBundleVersion: at most 4/2/2 digits; retries get their own build number.
if (( GITHUB_RUN_NUMBER < 1 || GITHUB_RUN_NUMBER > 999899 ||
      GITHUB_RUN_ATTEMPT < 1 || GITHUB_RUN_ATTEMPT > 99 )); then
  echo "::error::Workflow run/attempt exceeds the supported Apple build number range"
  exit 1
fi
build_number="$((GITHUB_RUN_NUMBER / 100 + 1)).$((GITHUB_RUN_NUMBER % 100)).${GITHUB_RUN_ATTEMPT}"
release_dir="$PWD/iosApp/build/release"
signing_dir=$(mktemp -d "$RUNNER_TEMP/ios-signing.XXXXXX")
keychain_path="$signing_dir/signing.keychain-db"
profile_path=""
firebase_path="$PWD/iosApp/iosApp/GoogleService-Info.plist"

cleanup() {
  security delete-keychain "$keychain_path" >/dev/null 2>&1 || true
  if [[ -n "$profile_path" ]]; then
    rm -f "$profile_path"
  fi
  rm -f "$firebase_path"
  rm -rf "$signing_dir"
}
trap cleanup EXIT
trap 'exit 130' INT
trap 'exit 143' TERM
umask 077
mkdir -p "$release_dir" "$signing_dir/private_keys"

printf '%s' "$FIREBASE_CONFIG_IOS" | base64 --decode > "$firebase_path"
printf '%s' "$IOS_DISTRIBUTION_CERTIFICATE_BASE64" | base64 --decode > "$signing_dir/certificate.p12"
printf '%s' "$IOS_PROVISIONING_PROFILE_BASE64" | base64 --decode > "$signing_dir/profile.mobileprovision"
printf '%s' "$APP_STORE_CONNECT_API_KEY_BASE64" | base64 --decode \
  > "$signing_dir/private_keys/AuthKey_${APP_STORE_CONNECT_API_KEY_ID}.p8"
security cms -D -i "$signing_dir/profile.mobileprovision" > "$signing_dir/profile.plist"

# Read the existing bundle ID; reject development/ad-hoc, expired or mismatched profiles before building.
profile_uuid=$(python3 - "$signing_dir/profile.plist" "$firebase_path" "$signing_dir/ExportOptions.plist" <<'PY'
import datetime
import os
import pathlib
import plistlib
import re
import sys
import uuid

def fail(message):
    sys.exit(f"::error::{message}")

config = pathlib.Path("iosApp/Configuration/Config.xcconfig").read_text()
match = re.search(r"^PRODUCT_BUNDLE_IDENTIFIER\s*=\s*(\S+)\s*$", config, re.MULTILINE)
if not match:
    fail("Cannot read PRODUCT_BUNDLE_IDENTIFIER from Config.xcconfig")
bundle_id = match[1]
team_id = os.environ["APPLE_TEAM_ID"]
with open(sys.argv[1], "rb") as source:
    profile = plistlib.load(source)
with open(sys.argv[2], "rb") as source:
    firebase = plistlib.load(source)
entitlements = profile.get("Entitlements", {})
if team_id not in profile.get("TeamIdentifier", []):
    fail("Provisioning profile does not belong to APPLE_TEAM_ID")
prefixes = profile.get("ApplicationIdentifierPrefix", [])
if entitlements.get("application-identifier") not in [f"{prefix}.{bundle_id}" for prefix in prefixes]:
    fail("Provisioning profile must match the explicit app bundle ID")
if (entitlements.get("get-task-allow", False) or "ProvisionedDevices" in profile
        or profile.get("ProvisionsAllDevices", False) or not entitlements.get("beta-reports-active", False)):
    fail("Use an App Store Connect distribution provisioning profile")
if profile["ExpirationDate"] <= datetime.datetime.now(datetime.timezone.utc).replace(tzinfo=None):
    fail("Provisioning profile has expired")
if firebase.get("BUNDLE_ID") != bundle_id:
    fail("FIREBASE_CONFIG_IOS does not match the app bundle ID")
profile_uuid = str(uuid.UUID(profile["UUID"])).upper()
with open(sys.argv[3], "wb") as target:
    plistlib.dump({
        "method": "app-store-connect",
        "destination": "export",
        "signingStyle": "manual",
        "teamID": team_id,
        "signingCertificate": "Apple Distribution",
        "provisioningProfiles": {bundle_id: profile_uuid},
        "manageAppVersionAndBuildNumber": False,
        "uploadSymbols": True,
    }, target)
print(profile_uuid)
PY
)

keychain_password=$(openssl rand -hex 32)
echo "::add-mask::$keychain_password"
security create-keychain -p "$keychain_password" "$keychain_path"
security set-keychain-settings -lut 21600 "$keychain_path"
security unlock-keychain -p "$keychain_password" "$keychain_path"
security import "$signing_dir/certificate.p12" -P "$IOS_DISTRIBUTION_CERTIFICATE_PASSWORD" \
  -A -t cert -f pkcs12 -k "$keychain_path"
security set-key-partition-list -S apple-tool:,apple:,codesign: -k "$keychain_password" "$keychain_path" >/dev/null
security list-keychains -d user -s "$keychain_path" "$HOME/Library/Keychains/login.keychain-db"
# Xcode 16+ profile location.
profile_path="$HOME/Library/Developer/Xcode/UserData/Provisioning Profiles/$profile_uuid.mobileprovision"
mkdir -p "$(dirname "$profile_path")"
cp "$signing_dir/profile.mobileprovision" "$profile_path"

echo "Archiving iOS Release, build $build_number"
# Xcode, not the shell, expands these build-setting expressions.
# shellcheck disable=SC2016
xcodebuild archive \
  -project iosApp/iosApp.xcodeproj \
  -scheme iosApp \
  -configuration Release \
  -destination 'generic/platform=iOS' \
  -archivePath "$release_dir/dubovozki.xcarchive" \
  -resultBundlePath "$release_dir/archive.xcresult" \
  -derivedDataPath "$RUNNER_TEMP/ios-derived-data" \
  "DEVELOPMENT_TEAM=$APPLE_TEAM_ID" \
  CODE_SIGN_STYLE=Manual \
  'CODE_SIGN_IDENTITY=Apple Distribution' \
  "PROVISIONING_PROFILE_SPECIFIER=$profile_uuid" \
  "CURRENT_PROJECT_VERSION=$build_number" \
  'FRAMEWORK_SEARCH_PATHS=$(inherited) $(SRCROOT)/../composeApp/build/xcode-frameworks/$(CONFIGURATION)/$(SDK_NAME)' \
  'OTHER_LDFLAGS=$(inherited) -framework ComposeApp' \
  2>&1 | tee "$release_dir/archive.log"

xcodebuild -exportArchive \
  -archivePath "$release_dir/dubovozki.xcarchive" \
  -exportPath "$release_dir/export" \
  -exportOptionsPlist "$signing_dir/ExportOptions.plist" \
  2>&1 | tee "$release_dir/export.log"

shopt -s nullglob
ipa_files=("$release_dir/export/"*.ipa)
if (( ${#ipa_files[@]} != 1 )); then
  echo "::error::Expected exactly one exported IPA"
  exit 1
fi

# altool searches ./private_keys for AuthKey_<key ID>.p8. Keep credentials outside artifacts.
cd "$signing_dir"
xcrun altool --upload-app --type ios --file "${ipa_files[0]}" \
  --apiKey "$APP_STORE_CONNECT_API_KEY_ID" \
  --apiIssuer "$APP_STORE_CONNECT_API_ISSUER_ID" \
  2>&1 | tee "$release_dir/upload.log"
# Keep Markdown backticks literal in the workflow summary.
# shellcheck disable=SC2016
printf '### iOS TestFlight\n\nUploaded build `%s` to App Store Connect. Apple processing is pending.\n' \
  "$build_number" >> "$GITHUB_STEP_SUMMARY"
