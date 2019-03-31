package io.snyk.plugins.teamcity.common;

public final class SnykSecurityRunnerConstants {
  public static final String RUNNER_TYPE = "snykSecurityRunner";
  public static final String RUNNER_DISPLAY_NAME = "Snyk Security";
  public static final String RUNNER_DESCRIPTION = "Runner for finding vulnerabilities in your dependencies";

  public static final String SEVERITY_THRESHOLD = "snyk.severityThreshold";
  public static final String MONITOR_PROJECT_ON_BUILD = "snyk.monitorProjectOnBuild";
  public static final String FILE = "snyk.file";
  public static final String ORGANISATION = "snyk.organisation";
  public static final String PROJECT_NAME = "snyk.projectName";
  public static final String ADDITIONAL_PARAMETERS = "snyk.additionalParameters";
  public static final String API_TOKEN = "secure:snyk.apiToken";
  public static final String VERSION = "snyk.version";

  public String getSeverityThreshold() {
    return SEVERITY_THRESHOLD;
  }

  public String getMonitorProjectOnBuild() {
    return MONITOR_PROJECT_ON_BUILD;
  }

  public String getFile() {
    return FILE;
  }

  public String getOrganisation() {
    return ORGANISATION;
  }

  public String getProjectName() {
    return PROJECT_NAME;
  }

  public String getAdditionalParameters() {
    return ADDITIONAL_PARAMETERS;
  }

  public String getApiToken() {
    return API_TOKEN;
  }

  public String getVersion() {
    return VERSION;
  }
}
