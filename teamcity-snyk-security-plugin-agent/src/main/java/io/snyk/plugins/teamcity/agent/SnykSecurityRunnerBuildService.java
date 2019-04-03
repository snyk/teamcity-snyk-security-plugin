package io.snyk.plugins.teamcity.agent;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.snyk.plugins.teamcity.common.runner.Platform;
import io.snyk.plugins.teamcity.common.runner.RunnerVersion;
import io.snyk.plugins.teamcity.common.runner.Runners;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildAgentSystemInfo;
import jetbrains.buildServer.agent.runner.BuildServiceAdapter;
import jetbrains.buildServer.agent.runner.ProgramCommandLine;
import jetbrains.buildServer.agent.runner.SimpleProgramCommandLine;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import static io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants.ADDITIONAL_PARAMETERS;
import static io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants.API_TOKEN;
import static io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants.CUSTOM_BUILD_TOOL_PATH;
import static io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants.FILE;
import static io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants.ORGANISATION;
import static io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants.PROJECT_NAME;
import static io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants.SEVERITY_THRESHOLD;
import static io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants.USE_CUSTOM_BUILD_TOOL_PATH;
import static io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants.VERSION;
import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.Arrays.asList;
import static jetbrains.buildServer.util.PropertiesUtil.getBoolean;
import static jetbrains.buildServer.util.StringUtil.nullIfEmpty;

public class SnykSecurityRunnerBuildService extends BuildServiceAdapter {

  private static final Logger LOG = Logger.getLogger(SnykSecurityRunnerBuildService.class);

  @NotNull
  @Override
  public ProgramCommandLine makeProgramCommandLine() throws RunBuildException {
    Platform platform = null;
    BuildAgentSystemInfo buildAgentSystemInfo = getAgentConfiguration().getSystemInfo();
    if (buildAgentSystemInfo.isUnix()) {
      platform = Platform.LINUX;
    } else if (buildAgentSystemInfo.isMac()) {
      platform = Platform.MAC_OS;
    } else if (buildAgentSystemInfo.isWindows()) {
      platform = Platform.WINDOWS;
    }

    File agentToolsDirectory = getAgentConfiguration().getAgentToolsDirectory();
    String version = getRunnerParameters().get(VERSION);
    RunnerVersion runner = Runners.getRunner(version);
    if (runner == null) {
      throw new RunBuildException(format("Snyk Security runner with version '%s' was not found. Please configure the build properly and retry.", version));
    }
    Path snykToolPath = Paths.get(agentToolsDirectory.getAbsolutePath(), "teamcity-snyk-security-plugin-runner", "bin", version, runner.getSnykToolPath(platform));

    String snykApiToken = getRunnerParameters().get(API_TOKEN);
    if (nullIfEmpty(snykApiToken) == null) {
      throw new RunBuildException("Snyk API token was not defined. Please configure the build properly and retry.");
    }
    Map<String, String> envVars = new HashMap<>(getEnvironmentVariables());
    envVars.put("SNYK_TOKEN", snykApiToken);
    addCustomBuildPathIfPresent(envVars);

    List<String> arguments = buildArguments();

    return new SimpleProgramCommandLine(envVars, getCheckoutDirectory().getAbsolutePath(), snykToolPath.toString(), arguments);
  }

  @NotNull
  private List<String> buildArguments() {
    List<String> arguments = new ArrayList<>();
    arguments.add("test");
    arguments.add("--json");

    String severityThreshold = getRunnerParameters().getOrDefault(SEVERITY_THRESHOLD, "low");
    arguments.add("--severity-threshold=" + severityThreshold);

    String file = getRunnerParameters().get(FILE);
    if (nullIfEmpty(file) != null) {
      arguments.add("--file=" + file);
    }

    String organisation = getRunnerParameters().get(ORGANISATION);
    if (nullIfEmpty(organisation) != null) {
      arguments.add("--org=" + organisation);
    }

    String projectName = getRunnerParameters().get(PROJECT_NAME);
    if (nullIfEmpty(projectName) != null) {
      arguments.add("--project-name=" + projectName);
    }

    String additionalParameters = getRunnerParameters().get(ADDITIONAL_PARAMETERS);
    if (nullIfEmpty(additionalParameters) != null) {
      arguments.addAll(asList(additionalParameters.split("\\s+")));
    }

    if (LOG.isDebugEnabled()) {
      LOG.debug(format("Runner arguments: {%s}", join(",", arguments)));
    }

    return arguments;
  }

  private void addCustomBuildPathIfPresent(@NotNull Map<String, String> envVars) {
    String useCustomBuildToolPath = getRunnerParameters().get(USE_CUSTOM_BUILD_TOOL_PATH);
    String customBuildToolPath = getRunnerParameters().get(CUSTOM_BUILD_TOOL_PATH);
    if (nullIfEmpty(useCustomBuildToolPath) == null && getBoolean(useCustomBuildToolPath)) {
      //TODO: add tool auto discovery (teamcity.tool.*)
    } else {
      String oldPath = envVars.get("PATH");
      envVars.put("PATH", oldPath + File.pathSeparator + customBuildToolPath);
    }
  }
}
