package io.snyk.plugins.teamcity.agent.commands;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import io.snyk.plugins.teamcity.common.runner.Platform;
import io.snyk.plugins.teamcity.common.runner.RunnerVersion;
import io.snyk.plugins.teamcity.common.runner.Runners;
import jetbrains.buildServer.TeamCityRuntimeException;
import jetbrains.buildServer.agent.BuildAgentSystemInfo;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.agent.runner.CommandExecution;
import jetbrains.buildServer.agent.runner.TerminationAction;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import static io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants.VERSION;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.apache.log4j.Logger.getLogger;

/**
 * Extends {@link jetbrains.buildServer.agent.runner.CommandExecution} interface.
 * Adds some methods for quicker access to Snyk tool and mapper.
 */
public abstract class SnykCommandAdapter implements CommandExecution {

  private static final Logger LOG = getLogger(SnykCommandAdapter.class);

  private final BuildRunnerContext buildRunnerContext;

  SnykCommandAdapter(@NotNull BuildRunnerContext buildRunnerContext) {
    this.buildRunnerContext = requireNonNull(buildRunnerContext);
  }

  /**
   * Returns arguments to be sent to snyk tool (<code>snyk</code>) or snyk report mapper (<code>snyk-to-html</code>).
   */
  abstract List<String> getArguments();

  @NotNull
  final BuildRunnerContext getRunnerContext() {
    return buildRunnerContext;
  }

  @Override
  public void beforeProcessStarted() {
  }

  @NotNull
  @Override
  public TerminationAction interruptRequested() {
    return TerminationAction.KILL_PROCESS_TREE;
  }

  @Override
  public boolean isCommandLineLoggingEnabled() {
    return false;
  }

  @Override
  public void onStandardOutput(@NotNull String text) {
    buildRunnerContext.getBuild().getBuildLogger().message(text);
  }

  @Override
  public void onErrorOutput(@NotNull String text) {
    buildRunnerContext.getBuild().getBuildLogger().warning(text);
  }

  @Override
  public void processStarted(@NotNull String programCommandLine, @NotNull File workingDirectory) {
    LOG.info("Command line arguments: " + programCommandLine);
  }

  @Override
  public void processFinished(int exitCode) {
    LOG.info("Process finished with exit code: " + exitCode);
  }

  @NotNull
  final String getSnykToolPath() {
    String version = buildRunnerContext.getRunnerParameters().get(VERSION);
    RunnerVersion runner = Runners.getRunner(version);
    if (runner == null) {
      throw new TeamCityRuntimeException(format("Snyk Security runner with version '%s' was not found. Please configure the build properly and retry.", version));
    }


    String agentToolsDirectory = buildRunnerContext.getBuild().getAgentConfiguration().getAgentToolsDirectory().getAbsolutePath();
    Platform platform = detectAgentPlatform();
    Path snykToolPath = Paths.get(agentToolsDirectory, "teamcity-snyk-security-plugin-runner", "bin", version, runner.getSnykToolPath(platform));
    if (!snykToolPath.toFile().exists()) {
      throw new TeamCityRuntimeException(format("Could not find '%s'", snykToolPath.toString()));
    }
    return snykToolPath.toString();
  }

  @NotNull
  private Platform detectAgentPlatform() {
    BuildAgentSystemInfo buildAgentSystemInfo = buildRunnerContext.getBuild().getAgentConfiguration().getSystemInfo();
    if (buildAgentSystemInfo.isUnix()) {
      return Platform.LINUX;
    } else if (buildAgentSystemInfo.isMac()) {
      return Platform.MAC_OS;
    } else if (buildAgentSystemInfo.isWindows()) {
      return Platform.WINDOWS;
    } else {
      throw new TeamCityRuntimeException("Could not detect OS on build agent: " + buildRunnerContext.getBuild().getAgentConfiguration().getName());
    }
  }
}
