# Contributing to TeamCity Snyk Security Plugin

:+1::tada: First off, thanks for taking the time to contribute! :tada::+1:

The following is a set of guidelines for contributing to TeamCity Snyk Security Plugin. These are mostly guidelines, not rules. Use your best judgment, and feel free to propose changes to this document in a pull request.

## Testing

- If you don't already have an instance of Teamcity available to use, you can download and run a local instance from the Jetbrains website [here](https://www.jetbrains.com/teamcity/download/)
- Download the Teamcity Snyk plugin [here](https://plugins.jetbrains.com/plugin/12227-snyk-security)
- Install the plugin following [this](https://docs.snyk.io/integrations/ci-cd-integrations/teamcity-integration-overview) guide
- Configure the plugin following [this](https://docs.snyk.io/integrations/ci-cd-integrations/teamcity-integration-overview/teamcity-integration-use-snyk-in-your-build) guide
  - Optionally, you can set your API endpoint if it differs from the default (https://snyk.io/). Do this by setting the `SNYK_API` environment variable in the build configuration's parameters page

## Releases

Before releasing, **please update the CLI**. This can be done by executing the `update-cli.sh` script. It is only tested
on macOS and may need adjustments on Linux for the `sed` command.

Releases are handled by the `release` Github Action. It will:
- Build the plugin assets
- Create a tagged release with the plugin assets
- Deploy the plugin to the JetBrains plugin portal

Releases are scheduled to occur every Tuesday.
