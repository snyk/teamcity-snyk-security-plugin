package io.snyk.plugins.teamcity.common;

public final class SnykSecurityRunnerConstants {
  public static final String RUNNER_TYPE = "snykSecurity";
  public static final String RUNNER_DISPLAY_NAME = "Snyk Security";
  public static final String RUNNER_DESCRIPTION = "Runner for finding vulnerabilities in your dependencies";

  public static final String SEVERITY_THRESHOLD = "severityThreshold";
  public static final String FAIL_ON_ISSUES = "failOnIssues";
  public static final String MONITOR_PROJECT_ON_BUILD = "monitorProjectOnBuild";
  public static final String FILE = "file";
  public static final String ORGANISATION = "organisation";
  public static final String PROJECT_NAME = "projectName";
  public static final String ADDITIONAL_PARAMETERS = "additionalParameters";
  public static final String API_TOKEN = "secure:apiToken";
  public static final String VERSION = "version";
  public static final String USE_CUSTOM_BUILD_TOOL_PATH = "useCustomBuildToolPath";
  public static final String CUSTOM_BUILD_TOOL_PATH = "customBuildToolPath";

  public static final String SNYK_ARTIFACTS_DIR = "snyk";
  public static final String SNYK_REPORT_JSON_FILE = "snyk_report.json";
  public static final String SNYK_REPORT_HTML_FILE = "snyk_report.html";
  public static final String SNYK_MONITOR_JSON_FILE = "snyk_monitor.json";

  public String getSeverityThreshold() {
    return SEVERITY_THRESHOLD;
  }

  public String getFailOnIssues() {
    return FAIL_ON_ISSUES;
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

  public String getUseCustomBuildToolPath() {
    return USE_CUSTOM_BUILD_TOOL_PATH;
  }

  public String getCustomBuildToolPath() {
    return CUSTOM_BUILD_TOOL_PATH;
  }
}
