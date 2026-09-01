#!/usr/bin/env bash

set -Euo pipefail

if (( $# < 3 )); then
  echo "Usage: $0 <simulator-udid> <video-name.mp4> <test-command> [args...]" >&2
  exit 2
fi

simulator_udid="$1"
video_name="$2"
shift 2

if [[ ! "$video_name" =~ ^[A-Za-z0-9._-]+\.mp4$ ]]; then
  echo "Video name must be a plain .mp4 filename" >&2
  exit 2
fi

recordings_dir="${RUNNER_TEMP:?RUNNER_TEMP must be set}/e2e-recordings"
host_video="$recordings_dir/$video_name"
recording_pid=""
recording_error=0

is_recording_running() {
  jobs -pr | grep -qx "$recording_pid"
}

finish_recording() {
  exit_code=$?
  trap - EXIT

  if [[ -n "$recording_pid" ]]; then
    if is_recording_running; then
      kill -INT "$recording_pid" >/dev/null 2>&1 || true
      for _ in {1..80}; do
        if ! is_recording_running; then
          break
        fi
        sleep 0.25
      done
    else
      echo "::error::iOS E2E screen recorder stopped before the test completed"
      recording_error=1
    fi

    if is_recording_running; then
      echo "::error::iOS E2E screen recorder did not stop after SIGINT"
      recording_error=1
      kill -TERM "$recording_pid" >/dev/null 2>&1 || true
      for _ in {1..20}; do
        if ! is_recording_running; then
          break
        fi
        sleep 0.25
      done
    fi

    if is_recording_running; then
      kill -KILL "$recording_pid" >/dev/null 2>&1 || true
    fi
    wait "$recording_pid" || true
  fi

  if [[ ! -s "$host_video" ]]; then
    echo "::error::iOS E2E screen recording was not created"
    recording_error=1
  fi

  if (( exit_code == 0 && recording_error != 0 )); then
    exit 1
  fi
  exit "$exit_code"
}

mkdir -p "$recordings_dir"
xcrun simctl io "$simulator_udid" recordVideo --codec=h264 --force "$host_video" &
recording_pid=$!
trap finish_recording EXIT

sleep 1
if ! is_recording_running; then
  wait "$recording_pid" || true
  echo "::error::iOS E2E screen recording could not be started"
  recording_error=1
  recording_pid=""
fi

"$@"
