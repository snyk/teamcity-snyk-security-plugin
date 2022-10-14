#!/usr/bin/env bash
set -ex
# shellcheck disable=SC2128
SCRIPT_PATH="$(readlink -f "${BASH_SOURCE}")"
SCRIPT_DIR="$(cd -P "$(dirname -- "${SCRIPT_PATH}")" >/dev/null 2>&1 && pwd)"

METADATA_CLI=$(curl https://static.snyk.io/cli/latest/release.json)
CLI_VERSION=$(echo "$METADATA_CLI" | jq -r '.version')

OUTPUT_DIR="$SCRIPT_DIR/teamcity-snyk-security-plugin-agent/src/runner/bin/$CLI_VERSION"

if [[ -d $OUTPUT_DIR ]]
then
  exit 0
fi

mkdir -p "$OUTPUT_DIR"

curl -o "$OUTPUT_DIR/snyk-alpine" "https://static.snyk.io/cli/v$CLI_VERSION/snyk-alpine"
curl -o "$OUTPUT_DIR/snyk-macos" "https://static.snyk.io/cli/v$CLI_VERSION/snyk-macos"
curl -o "$OUTPUT_DIR/snyk-linux" "https://static.snyk.io/cli/v$CLI_VERSION/snyk-linux"
curl -o "$OUTPUT_DIR/snyk-win.exe" "https://static.snyk.io/cli/v$CLI_VERSION/snyk-win.exe"
curl -o "$OUTPUT_DIR/snyk-linux-arm64" "https://static.snyk.io/cli/v$CLI_VERSION/snyk-linux-arm64"

curl -o "$OUTPUT_DIR/snyk-to-html-alpine" "https://static.snyk.io/snyk-to-html/latest/snyk-to-html-alpine"
curl -o "$OUTPUT_DIR/snyk-to-html-macos" "https://static.snyk.io/snyk-to-html/latest/snyk-to-html-macos"
curl -o "$OUTPUT_DIR/snyk-to-html-linux" "https://static.snyk.io/snyk-to-html/latest/snyk-to-html-linux"
curl -o "$OUTPUT_DIR/snyk-to-html-win.exe" "https://static.snyk.io/snyk-to-html/latest/snyk-to-html-win.exe"
chmod -R +x "$OUTPUT_DIR"


OLD_DIR=$(ls "$OUTPUT_DIR/.."| sort | head -1)

sed -i '' "s/$OLD_DIR/$CLI_VERSION/g" teamcity-snyk-security-plugin-agent/src/assembly/teamcity-plugin-runner.xml
sed -i '' "s/$OLD_DIR/$CLI_VERSION/g" teamcity-snyk-security-plugin-common/src/main/java/io/snyk/plugins/teamcity/common/runner/Runners.java

# shellcheck disable=SC2012
if [[ $OLD_DIR != "$CLI_VERSION" ]]
then
  git rm -r "$OUTPUT_DIR/../$OLD_DIR"
fi

git add "$OUTPUT_DIR"
git add teamcity-snyk-security-plugin-agent/src/assembly/teamcity-plugin-runner.xml
git add teamcity-snyk-security-plugin-common/src/main/java/io/snyk/plugins/teamcity/common/runner/Runners.java

git commit -m "chore: updated CLI to $CLI_VERSION"

echo "Done. Don't forget to update the changelog."



