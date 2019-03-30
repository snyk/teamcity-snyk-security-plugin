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
      if (isEmptyOrNull(properties.get("secure:snyk.apiToken"))) {
        findings.add(new InvalidProperty("secure:snyk.apiToken", "Snyk API token must be specified."));
      }
      if (isEmptyOrNull(properties.get("snyk.version"))) {
        findings.add(new InvalidProperty("snyk.version", "Please define a Snyk version."));
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
    return new HashMap<>(0);
  }

  @NotNull
  @Override
  public String describeParameters(@NotNull Map<String, String> parameters) {
    String severityThreshold = parameters.get("snyk.severityThreshold");
    String monitorProjectOnBuild = parameters.get("snyk.monitorProjectOnBuild");
    return String.format("Severity threshold: %s%nMonitor project on build: %s", severityThreshold, monitorProjectOnBuild);
  }
}
