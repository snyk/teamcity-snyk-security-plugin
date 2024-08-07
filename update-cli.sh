#!/usr/bin/env bash
set -ex
# shellcheck disable=SC2128
SCRIPT_PATH="$(readlink -f "${BASH_SOURCE}")"
SCRIPT_DIR="$(cd -P "$(dirname -- "${SCRIPT_PATH}")" >/dev/null 2>&1 && pwd)"

METADATA_CLI=$(curl https://static.snyk.io/cli/latest/release.json)
echo "Using latest CLI version, which is $(echo "$METADATA_CLI" | jq -r '.version')"

SNYK_INTEGRATION_NAME='TEAMCITY_PLUGIN'
CLI_VERSION='latest'
OUTPUT_DIR="$SCRIPT_DIR/teamcity-snyk-security-plugin-agent/src/runner/bin/$CLI_VERSION"

if [[ -d $OUTPUT_DIR ]]
then
  rm -r $OUTPUT_DIR
fi

mkdir -p "$OUTPUT_DIR"

curl --compressed -o "$OUTPUT_DIR/snyk-alpine" "https://downloads.snyk.io/cli/$CLI_VERSION/snyk-alpine?utm_source=$SNYK_INTEGRATION_NAME" || curl --compressed -o "$OUTPUT_DIR/snyk-alpine" "https://static.snyk.io/cli/$CLI_VERSION/snyk-alpine"
curl --compressed -o "$OUTPUT_DIR/snyk-macos" "https://downloads.snyk.io/cli/$CLI_VERSION/snyk-macos?utm_source=$SNYK_INTEGRATION_NAME" || curl --compressed -o "$OUTPUT_DIR/snyk-macos" "https://static.snyk.io/cli/$CLI_VERSION/snyk-macos"
curl --compressed -o "$OUTPUT_DIR/snyk-linux" "https://downloads.snyk.io/cli/$CLI_VERSION/snyk-linux?utm_source=$SNYK_INTEGRATION_NAME" || curl --compressed -o "$OUTPUT_DIR/snyk-linux" "https://static.snyk.io/cli/$CLI_VERSION/snyk-linux"
curl --compressed -o "$OUTPUT_DIR/snyk-win.exe" "https://downloads.snyk.io/cli/$CLI_VERSION/snyk-win.exe?utm_source=$SNYK_INTEGRATION_NAME" || curl --compressed -o "$OUTPUT_DIR/snyk-win.exe" "https://static.snyk.io/cli/$CLI_VERSION/snyk-win.exe"
curl --compressed -o "$OUTPUT_DIR/snyk-linux-arm64" "https://downloads.snyk.io/cli/$CLI_VERSION/snyk-linux-arm64?utm_source=$SNYK_INTEGRATION_NAME" || curl --compressed -o "$OUTPUT_DIR/snyk-linux-arm64" "https://static.snyk.io/cli/$CLI_VERSION/snyk-linux-arm64"

curl --compressed -o "$OUTPUT_DIR/snyk-to-html-alpine" "https://downloads.snyk.io/snyk-to-html/latest/snyk-to-html-alpine?utm_source=$SNYK_INTEGRATION_NAME" || curl --compressed -o "$OUTPUT_DIR/snyk-to-html-alpine" "https://static.snyk.io/snyk-to-html/latest/snyk-to-html-alpine"
curl --compressed -o "$OUTPUT_DIR/snyk-to-html-macos" "https://downloads.snyk.io/snyk-to-html/latest/snyk-to-html-macos?utm_source=$SNYK_INTEGRATION_NAME" || curl --compressed -o "$OUTPUT_DIR/snyk-to-html-macos" "https://static.snyk.io/snyk-to-html/latest/snyk-to-html-macos"
curl --compressed -o "$OUTPUT_DIR/snyk-to-html-linux" "https://downloads.snyk.io/snyk-to-html/latest/snyk-to-html-linux?utm_source=$SNYK_INTEGRATION_NAME" || curl --compressed -o "$OUTPUT_DIR/snyk-to-html-linux" "https://static.snyk.io/snyk-to-html/latest/snyk-to-html-linux"
curl --compressed -o "$OUTPUT_DIR/snyk-to-html-win.exe" "https://downloads.snyk.io/snyk-to-html/latest/snyk-to-html-win.exe?utm_source=$SNYK_INTEGRATION_NAME" || curl --compressed -o "$OUTPUT_DIR/snyk-to-html-win.exe" "https://static.snyk.io/snyk-to-html/latest/snyk-to-html-win.exe"
chmod -R +x "$OUTPUT_DIR"

echo "Done. Don't forget to update the changelog."



