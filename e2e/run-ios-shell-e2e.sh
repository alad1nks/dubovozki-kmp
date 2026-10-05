#!/usr/bin/env bash

set -euo pipefail

# Run from the repository root. Both PR and nightly use the same preparation.
plist=iosApp/iosApp/GoogleService-Info.plist
project_id=$(/usr/libexec/PlistBuddy -c 'Print :PROJECT_ID' "$plist")
database_url=$(/usr/libexec/PlistBuddy -c 'Print :DATABASE_URL' "$plist")
# useEmulator changes the host, but the SDK keeps the configured database namespace.
namespace=$(node -e 'console.log(new URL(process.argv[1]).hostname.split(".")[0])' "$database_url")
export TEST_RUNNER_E2E_FIREBASE_NAMESPACE="$namespace"

e2e/web/node_modules/.bin/firebase emulators:start --project "$project_id" --only database > firebase-emulator.log 2>&1 &
emulator_pid=$!
trap 'kill "$emulator_pid" 2>/dev/null || true; wait "$emulator_pid" 2>/dev/null || true' EXIT
node e2e/wait-for-firebase.mjs "$namespace"
curl --fail --silent --show-error --output /dev/null --request PUT \
  --header 'Content-Type: application/json' --data-binary @e2e/fixtures/firebase/happy.json \
  "http://127.0.0.1:9000/.json?ns=$namespace"

simulator_udid=${SIMULATOR_UDID:-$(xcrun simctl list devices available -j | jq -r '[.devices[][] | select(.name | startswith("iPhone"))][0].udid')}
xcrun simctl boot "$simulator_udid" || true
xcrun simctl bootstatus "$simulator_udid" -b
export RUNNER_TEMP=${RUNNER_TEMP:-${TMPDIR:-/tmp}}
bash e2e/record-ios-e2e.sh "$simulator_udid" ios-xcuitest.mp4 \
  xcodebuild test -project iosApp/iosApp.xcodeproj -scheme iosApp \
  -destination "platform=iOS Simulator,id=$simulator_udid" \
  -parallel-testing-enabled NO \
  -resultBundlePath iosApp/TestResults.xcresult
