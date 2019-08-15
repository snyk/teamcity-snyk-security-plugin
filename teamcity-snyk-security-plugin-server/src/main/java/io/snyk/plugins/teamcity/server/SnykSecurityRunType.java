package io.snyk.plugins.teamcity.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants;
import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.serverSide.RunType;
import jetbrains.buildServer.serverSide.RunTypeRegistry;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants.API_TOKEN;
import static io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants.CUSTOM_BUILD_TOOL_PATH;
import static io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants.FAIL_ON_ISSUES;
import static io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants.MONITOR_PROJECT_ON_BUILD;
import static io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants.SEVERITY_THRESHOLD;
import static io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants.USE_CUSTOM_BUILD_TOOL_PATH;
import static io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants.VERSION;
import static jetbrains.buildServer.util.PropertiesUtil.getBoolean;
import static jetbrains.buildServer.util.PropertiesUtil.isEmptyOrNull;

public class SnykSecurityRunType extends RunType {

  @NotNull
  private final PluginDescriptor pluginDescriptor;

  public SnykSecurityRunType(@NotNull final RunTypeRegistry runTypeRegistry, @NotNull final PluginDescriptor pluginDescriptor) {
    this.pluginDescriptor = pluginDescriptor;
    runTypeRegistry.registerRunType(this);
  }

  @NotNull
  @Override
  public String getType() {
    return SnykSecurityRunnerConstants.RUNNER_TYPE;
  }

  @NotNull
  @Override
  public String getDisplayName() {
    return SnykSecurityRunnerConstants.RUNNER_DISPLAY_NAME;
  }

  @NotNull
  @Override
  public String getDescription() {
    return SnykSecurityRunnerConstants.RUNNER_DESCRIPTION;
  }

  @Nullable
  @Override
  public PropertiesProcessor getRunnerPropertiesProcessor() {
    return properties -> {
      if (properties == null) {
        return Collections.emptyList();
      }

      List<InvalidProperty> findings = new ArrayList<>(0);
      if (isEmptyOrNull(properties.get(API_TOKEN))) {
        findings.add(new InvalidProperty(API_TOKEN, "Snyk API token must be specified."));
      }
      if (isEmptyOrNull(properties.get(VERSION))) {
        findings.add(new InvalidProperty(VERSION, "Please define a Snyk version."));
      }
      if (getBoolean(properties.get(USE_CUSTOM_BUILD_TOOL_PATH)) && isEmptyOrNull(properties.get(CUSTOM_BUILD_TOOL_PATH))) {
        findings.add(new InvalidProperty(CUSTOM_BUILD_TOOL_PATH, "Please define a custom build tool path."));
      }
      return findings;
    };
  }

  @Nullable
  @Override
  public String getEditRunnerParamsJspFilePath() {
    return pluginDescriptor.getPluginResourcesPath("editSnykSecurityRunnerParameters.jsp");
  }

  @Nullable
  @Override
  public String getViewRunnerParamsJspFilePath() {
    return pluginDescriptor.getPluginResourcesPath("viewSnykSecurityRunnerParameters.jsp");
  }

  @Nullable
  @Override
  public Map<String, String> getDefaultRunnerProperties() {
    Map<String, String> defaultProperties = new HashMap<>(3);
    defaultProperties.put(SEVERITY_THRESHOLD, "low");
    defaultProperties.put(FAIL_ON_ISSUES, "true");
    defaultProperties.put(MONITOR_PROJECT_ON_BUILD, "true");
    return defaultProperties;
  }

  @NotNull
  @Override
  public String describeParameters(@NotNull Map<String, String> parameters) {
    String severityThreshold = isEmptyOrNull(parameters.get(SEVERITY_THRESHOLD)) ? "low (default)" : parameters.get(SEVERITY_THRESHOLD);
    String monitorProjectOnBuild = getBoolean(parameters.get(MONITOR_PROJECT_ON_BUILD)) ? "ON" : "OFF";
    return String.format("Severity threshold: %s%nMonitor project on build: %s", severityThreshold, monitorProjectOnBuild);
  }
}
