package io.snyk.plugins.teamcity.common;

public final class SnykSecurityRunnerConstants {
  public static final String RUNNER_TYPE = "snykSecurityRunner";
  public static final String RUNNER_DISPLAY_NAME = "Snyk Security";
  public static final String RUNNER_DESCRIPTION = "Runner for finding vulnerabilities in your dependencies";

  private SnykSecurityRunnerConstants() {
    // squid:S1118
  }
}
