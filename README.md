# TeamCity Snyk Security Plugin

[![Known Vulnerabilities](https://snyk.io/test/github/snyk/teamcity-snyk-security-plugin/badge.svg)](https://snyk.io/test/github/snyk/teamcity-snyk-security-plugin)
[![Build Status](https://github.com/snyk/teamcity-snyk-security-plugin/actions/workflows/release.yml/badge.svg)](https://github.com/snyk/teamcity-snyk-security-plugin/actions/workflows/release.yml)

Enables TeamCity integration with Snyk and allows users to test their applications against the [Snyk vulnerability database](https://snyk.io/vuln).

## Table of Contents

* [Overview](#overview)
* [Installation](#installation)
* [Usage](#usage)
* [Contributions](#contributions)
* [License](#license)


## Overview

Plugin supports following operations:
* test projects for known vulnerabilities
* take an application dependencies snapshot for continuous monitoring by Snyk
* create an HTML report displaying the vulnerabilities discovered


## Installation

You can [download the plugin](https://plugins.jetbrains.com/plugin/12227-snyk-security) and install it as an [additional plugin](https://confluence.jetbrains.com/display/TCDL/Installing+Additional+Plugins) for TeamCity 2018.2+.


## Usage

Add **Snyk Security** step to build configuration and adjust parameters you need

![Snyk Security build step](.github/images/snyk-security_build-step.png)

Available configuration parameters:
* **Severity threshold** - Only report vulnerabilities of provided level or higher.
* **Monitor project on build** - Take a current application dependencies snapshot for continuous monitoring by Snyk.
* **File** - The path to the application manifest file to be scanned by Snyk.
* **Organisation** - The Snyk organisation in which this project should be tested and monitored.
* **Project name** - A custom name for the Snyk project created for this TeamCity project on every build.
* **Additional parameters** - Refer to the [Snyk CLI](https://snyk.io/docs/using-snyk/) help page for information on additional parameters.
* **Snyk API token** - The ID for the API token to be used to authenticate with Snyk.
* **Snyk version** - The bundled Snyk CLI version.
* **Custom build tool path** - Specify the path to the build tool used for the project if the checkbox **Use custom build tool path** is selected.
Otherwise, auto-discover mode will be activated.


## Contributing

To ensure the long-term stability and quality of this project, we are moving to a closed-contribution model effective August 2025. This change allows our core team to focus on a centralized development roadmap and rigorous quality assurance, which is essential for a component with such extensive usage.

All of our development will remain public for transparency. We thank the community for its support and valuable contributions.

## Getting Support

GitHub issues have been disabled on this repository as part of our move to a closed-contribution model. The Snyk support team does not actively monitor GitHub issues on any Snyk development project.

For help with Snyk products, please use the [Snyk support page](https://support.snyk.io/), which is the fastest way to get assistance.


## License

This project is licensed under the [Apache License, Version 2.0](LICENSE).
