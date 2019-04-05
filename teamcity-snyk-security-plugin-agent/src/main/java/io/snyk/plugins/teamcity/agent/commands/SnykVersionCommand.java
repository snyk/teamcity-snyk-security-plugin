package io.snyk.plugins.teamcity.agent.commands;

import java.util.List;

import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.agent.runner.ProgramCommandLine;
import jetbrains.buildServer.agent.runner.SimpleProgramCommandLine;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import static java.util.Collections.singletonList;
import static jetbrains.buildServer.util.StringUtil.nullIfEmpty;
import static org.apache.log4j.Logger.getLogger;

public class SnykVersionCommand extends SnykCommandAdapter {

  private static final Logger LOG = getLogger(SnykVersionCommand.class);

  public SnykVersionCommand(@NotNull BuildRunnerContext buildRunnerContext) {
    super(buildRunnerContext);
  }

  @NotNull
  @Override
  public ProgramCommandLine makeProgramCommandLine() {
    String snykToolPath = getSnykToolPath();
    return new SimpleProgramCommandLine(getRunnerContext().getBuildParameters().getEnvironmentVariables(),
                                        getRunnerContext().getWorkingDirectory().getAbsolutePath(),
                                        snykToolPath,
                                        getArguments());
  }

  @Override
  List<String> getArguments() {
    return singletonList("--version");
  }

  @Override
  public void beforeProcessStarted() {
    getRunnerContext().getBuild().getBuildLogger().message("Determining Snyk tool version...");
  }

  @Override
  public void onStandardOutput(@NotNull String text) {
    //'snyk --version' command adds a carriage return after printing version, so we don't want to catch empty string
    if (nullIfEmpty(text) == null) {
      return;
    }
    getRunnerContext().getBuild().getBuildLogger().message("Snyk tool version: " + text);
    LOG.info("Snyk tool version: " + text);
  }
}
