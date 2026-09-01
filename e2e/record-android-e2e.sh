#!/usr/bin/env bash

set -Euo pipefail

if (( $# < 2 )); then
  echo "Usage: $0 <video-name.mp4> <test-command> [args...]" >&2
  exit 2
fi

video_name="$1"
shift

if [[ ! "$video_name" =~ ^[A-Za-z0-9._-]+\.mp4$ ]]; then
  echo "Video name must be a plain .mp4 filename" >&2
  exit 2
fi

recordings_dir="${RUNNER_TEMP:?RUNNER_TEMP must be set}/e2e-recordings"
video_stem="${video_name%.mp4}"
recording_pid=""
current_device_video=""
test_pid=""
recording_error=0
part=1
segment_started_at=0
declare -a device_segments=()
declare -a host_segments=()

is_test_running() {
  jobs -pr | grep -qx "$test_pid"
}

recording_status() {
  if [[ -z "$recording_pid" ]]; then
    return 1
  fi

  local process_name
  if ! process_name=$(
    adb shell \
      "if [ -r /proc/$recording_pid/comm ]; then cat /proc/$recording_pid/comm; else echo __STOPPED__; fi" \
      2>/dev/null
  ); then
    return 2
  fi
  process_name="${process_name//$'\r'/}"

  if [[ "$process_name" == "screenrecord" ]]; then
    return 0
  fi
  return 1
}

wait_for_recording_to_stop() {
  local attempts="$1"
  local attempt
  local status

  for ((attempt = 0; attempt < attempts; attempt++)); do
    recording_status
    status=$?
    if (( status == 1 )); then
      return 0
    fi
    sleep 0.25
  done

  recording_status
  status=$?
  (( status == 1 ))
}

start_current_recording() {
  current_device_video="/sdcard/$video_stem-part$part.mp4"
  adb shell rm -f "$current_device_video"
  segment_started_at=$SECONDS

  recording_pid=$(
    adb shell \
      "screenrecord --bit-rate 4000000 --time-limit 180 '$current_device_video' </dev/null >/dev/null 2>&1 & echo \$!" |
      tr -d '\r'
  )
  start_exit=$?

  if (( start_exit != 0 )) || [[ ! "$recording_pid" =~ ^[0-9]+$ ]]; then
    echo "::error::Android E2E screen recording could not be started"
    recording_pid=""
    current_device_video=""
    return 1
  fi

  sleep 1
  recording_status
  start_status=$?
  if (( start_status != 0 )); then
    echo "::error::Android E2E screen recording stopped before the test segment started"
    if (( start_status == 2 )); then
      echo "::error::Android E2E screen recorder status could not be read through adb"
    fi
    discard_current_segment || true
    return 1
  fi

  return 0
}

stop_current_recording() {
  recording_status
  stop_status=$?
  if (( stop_status == 1 )); then
    return 0
  fi

  adb shell kill -2 "$recording_pid" >/dev/null 2>&1 || true
  if wait_for_recording_to_stop 40; then
    return 0
  fi

  echo "::error::Android E2E screen recorder did not stop cleanly"
  adb shell kill -15 "$recording_pid" >/dev/null 2>&1 || true
  if wait_for_recording_to_stop 20; then
    return 1
  fi

  adb shell kill -9 "$recording_pid" >/dev/null 2>&1 || true
  if ! wait_for_recording_to_stop 8; then
    echo "::error::Android E2E screen recorder could not be terminated"
  fi
  return 1
}

remember_current_segment() {
  if [[ -z "$current_device_video" ]]; then
    return
  fi

  device_segments+=("$current_device_video")
  host_segments+=("$recordings_dir/$video_stem-part${#device_segments[@]}.mp4")
  recording_pid=""
  current_device_video=""
}

discard_current_segment() {
  if [[ -z "$current_device_video" ]]; then
    recording_pid=""
    return 0
  fi

  recording_status
  discard_status=$?
  if (( discard_status != 1 )); then
    adb shell kill -9 "$recording_pid" >/dev/null 2>&1 || true
    if ! wait_for_recording_to_stop 8; then
      echo "::error::Android E2E screen recorder is still running after forced termination"
      return 1
    fi
  fi

  adb shell rm -f "$current_device_video" >/dev/null 2>&1 || true
  recording_pid=""
  current_device_video=""
}

finalize_current_segment() {
  recording_status
  finalize_status=$?
  if (( finalize_status == 2 )); then
    echo "::error::Android E2E screen recorder status could not be read through adb"
    discard_current_segment || true
    return 1
  fi

  if (( finalize_status == 1 && SECONDS - segment_started_at < 170 )); then
    echo "::error::Android E2E screen recorder stopped before the test completed"
    discard_current_segment || true
    return 1
  fi

  if stop_current_recording; then
    remember_current_segment
    return 0
  fi

  discard_current_segment || true
  return 1
}

download_segments() {
  segment_error=0

  for index in "${!device_segments[@]}"; do
    device_segment="${device_segments[$index]}"
    host_segment="${host_segments[$index]}"
    if ! adb pull "$device_segment" "$host_segment"; then
      echo "::error::Android E2E screen recording segment could not be downloaded"
      segment_error=1
    elif [[ ! -s "$host_segment" ]]; then
      echo "::error::Android E2E screen recording segment is empty"
      segment_error=1
    fi
  done

  return "$segment_error"
}

finish_on_exit() {
  exit_code=$?
  trap - EXIT

  if [[ -n "$recording_pid" ]]; then
    if ! finalize_current_segment; then
      recording_error=1
    fi
  fi

  if [[ -n "$test_pid" ]] && is_test_running; then
    kill -TERM "$test_pid" >/dev/null 2>&1 || true
    wait "$test_pid" || true
  fi

  if [[ -n "$recording_pid" ]]; then
    discard_current_segment || true
  fi

  if (( ${#device_segments[@]} > 0 )) && ! download_segments; then
    recording_error=1
  fi

  if (( exit_code == 0 && recording_error != 0 )); then
    exit 1
  fi
  exit "$exit_code"
}

mkdir -p "$recordings_dir"

# Keep compilation outside each device recorder segment's 180-second hard limit.
./gradlew :androidApp:assembleDebug :androidApp:assembleDebugAndroidTest
prebuild_exit=$?
if (( prebuild_exit != 0 )); then
  exit "$prebuild_exit"
fi

trap finish_on_exit EXIT
if ! start_current_recording; then
  recording_error=1
fi

"$@" &
test_pid=$!

while is_test_running && [[ -n "$recording_pid" ]]; do
  recording_status
  loop_status=$?

  if ! is_test_running; then
    break
  fi

  if (( loop_status == 0 )); then
    sleep 0.25
    continue
  fi

  if (( loop_status == 2 )); then
    echo "::error::Android E2E screen recorder status could not be read through adb"
    recording_error=1
    stop_current_recording || true
    discard_current_segment || true
    break
  fi

  if (( SECONDS - segment_started_at < 170 )); then
    echo "::error::Android E2E screen recorder stopped unexpectedly"
    recording_error=1
    discard_current_segment || true
    break
  fi

  # screenrecord exits on its own at Android's 180-second limit.
  remember_current_segment
  part=$((part + 1))
  if ! start_current_recording; then
    recording_error=1
    break
  fi
done

wait "$test_pid"
test_exit=$?
test_pid=""

if [[ -n "$recording_pid" ]]; then
  if ! finalize_current_segment; then
    recording_error=1
  fi
fi

if [[ -n "$recording_pid" ]]; then
  discard_current_segment || true
fi

if (( ${#device_segments[@]} == 0 )); then
  echo "::error::Android E2E screen recording was not created"
  recording_error=1
elif ! download_segments; then
  recording_error=1
fi

trap - EXIT
if (( test_exit == 0 && recording_error != 0 )); then
  exit 1
fi
exit "$test_exit"
