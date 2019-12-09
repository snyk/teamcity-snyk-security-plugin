package io.snyk.plugins.teamcity.agent.commands;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import io.snyk.plugins.teamcity.common.runner.Platform;
import io.snyk.plugins.teamcity.common.runner.RunnerVersion;
import io.snyk.plugins.teamcity.common.runner.Runners;
import jetbrains.buildServer.TeamCityRuntimeException;
import jetbrains.buildServer.agent.BuildAgentSystemInfo;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.agent.runner.BuildServiceAdapter;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import static io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants.VERSION;
import static java.lang.String.format;

public abstract class SnykBuildServiceAdapter extends BuildServiceAdapter {

  private static final Logger LOG = Logger.getLogger(SnykBuildServiceAdapter.class);

  abstract List<String> getArguments();

  @NotNull
  public final BuildRunnerContext getBuildRunnerContext() {
    return getRunnerContext();
  }

  String getSnykToolPath() {
    String version = getRunnerParameters().get(VERSION);
    RunnerVersion runner = Runners.getRunner(version);
    if (runner == null) {
      LOG.warn(format("Snyk Security runner with version '%s' was not found. Default runner will be used.", version));
      version = Runners.getDefaultRunnerVersion();
      runner = Runners.getDefaultRunner();
    }

    String agentToolsDirectory = getAgentConfiguration().getAgentToolsDirectory().getAbsolutePath();
    Platform platform = detectAgentPlatform();
    Path snykToolPath = Paths.get(agentToolsDirectory, "teamcity-snyk-security-plugin-runner", "bin", version, runner.getSnykToolPath(platform));
    if (!snykToolPath.toFile().exists()) {
      throw new TeamCityRuntimeException(format("Could not found '%s'", snykToolPath.toString()));
    }
    return snykToolPath.toString();
  }

  String getReportMapperPath() {
    String version = getRunnerParameters().get(VERSION);
    RunnerVersion runner = Runners.getRunner(version);
    if (runner == null) {
      LOG.warn(format("Snyk Security runner with version '%s' was not found. Default runner will be used.", version));
      version = Runners.getDefaultRunnerVersion();
      runner = Runners.getDefaultRunner();
    }

    String agentToolsDirectory = getAgentConfiguration().getAgentToolsDirectory().getAbsolutePath();
    Platform platform = detectAgentPlatform();
    Path reportMapperPath = Paths.get(agentToolsDirectory, "teamcity-snyk-security-plugin-runner", "bin", version, runner.getReportMapperPath(platform));
    if (!reportMapperPath.toFile().exists()) {
      throw new TeamCityRuntimeException(format("Could not found '%s'", reportMapperPath.toString()));
    }
    return reportMapperPath.toString();
  }

  private Platform detectAgentPlatform() {
    BuildAgentSystemInfo buildAgentSystemInfo = getAgentConfiguration().getSystemInfo();
    if (buildAgentSystemInfo.isUnix() && !buildAgentSystemInfo.isMac()) {
      return Platform.LINUX;
    } else if (buildAgentSystemInfo.isMac()) {
      return Platform.MAC_OS;
    } else if (buildAgentSystemInfo.isWindows()) {
      return Platform.WINDOWS;
    } else {
      throw new TeamCityRuntimeException("Could not detect OS on build agent: " + getAgentConfiguration().getName());
    }
  }

  @Override
  public boolean isCommandLineLoggingEnabled() {
    return true;
  }

  @NotNull
  @Override
  public BuildFinishedStatus getRunResult(int exitCode) {
    if (exitCode == 0) {
      return BuildFinishedStatus.FINISHED_SUCCESS;
    }
    return getBuild().getFailBuildOnExitCode() ? BuildFinishedStatus.FINISHED_WITH_PROBLEMS : BuildFinishedStatus.FINISHED_SUCCESS;
  }
}
