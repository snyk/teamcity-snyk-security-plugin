# Contributing to TeamCity Snyk Security Plugin

:+1::tada: First off, thanks for taking the time to contribute! :tada::+1:

The following is a set of guidelines for contributing to TeamCity Snyk Security Plugin. These are mostly guidelines, not rules. Use your best judgment, and feel free to propose changes to this document in a pull request.

## Testing

- If you don't already have an instance of Teamcity available to use, you can download and run a local instance from the Jetbrains website [here](https://www.jetbrains.com/teamcity/download/)
- Download the Teamcity Snyk plugin [here](https://plugins.jetbrains.com/plugin/12227-snyk-security)
- Install the plugin following [this](https://docs.snyk.io/integrations/ci-cd-integrations/teamcity-integration-overview) guide
- Configure the plugin following [this](https://docs.snyk.io/integrations/ci-cd-integrations/teamcity-integration-overview/teamcity-integration-use-snyk-in-your-build) guide
  - Optionally, you can set your API endpoint if it differs from the default (https://snyk.io/). Do this by setting the `SNYK_API` environment variable in the build configuration's parameters page

## Local Development
When making code changes, it can be useful to QA test the plugin locally. The following outlines how to do so.
### Build the plugin .zip
Firstly we need to build the plugin .zip file, this zip file is used our TeamCity instance
- Run `./mvnw clean verify`
- Navigate to `/Users/<user.name>/<path-to-team-city-repo>/distribution/target`
- `teamcity-snyk-security-plugin.zip` is located here

### Run your local TeamCity instance
If you haven't already, download the local instance from the Jetbrains website
- Start your local instance via `<TeamCityDir>/bin/runAll.sh start`
- Login as `SuperUser` using the `AuthorizationToken` found in `<TeamCityDir>/buildAgent/logs/teamcity-agent.log`
- Navigate to the plugin section as outlined in [Testing](#testing)
- Install the plugin via the `Upload .zip file` option
- Upload the `teamcity-snyk-security-plugin.zip`
- Configure the plugin as usual 

### Configuring CLI / Snyk-to-HTML versions
If you would like to test different versions of the CLI and Snyk-to-HTML binaries when running a local instance, then you must replace the binaries are located in: 

`<TeamCityDir>/buildAgent/tools/teamcity-snyk-security-plugin-runner/bin/<CLI_VERSION>/` 

## Releases

Before releasing, **please update the CLI**. This can be done by executing the `update-cli.sh` script. It is only tested
on macOS and may need adjustments on Linux for the `sed` command.

Releases are handled by the `release` Github Action. It will:
- Build the plugin assets
- Create a tagged release with the plugin assets
- Deploy the plugin to the JetBrains plugin portal

Releases are scheduled to occur every Tuesday.
