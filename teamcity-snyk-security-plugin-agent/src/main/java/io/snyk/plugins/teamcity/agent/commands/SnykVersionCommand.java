package io.snyk.plugins.teamcity.agent.commands;

import java.util.List;

import jetbrains.buildServer.agent.runner.ProgramCommandLine;
import jetbrains.buildServer.agent.runner.SimpleProgramCommandLine;
import org.jetbrains.annotations.NotNull;

import static java.util.Collections.singletonList;

public class SnykVersionCommand extends SnykBuildServiceAdapter {

  @NotNull
  @Override
  public ProgramCommandLine makeProgramCommandLine() {
    String snykToolPath = getSnykToolPath();

    return new SimpleProgramCommandLine(getBuildParameters().getEnvironmentVariables(),
                                        getWorkingDirectory().getAbsolutePath(),
                                        snykToolPath,
                                        getArguments());
  }

  @Override
  public void beforeProcessStarted() {
    getBuild().getBuildLogger().message("Determining Snyk tool version...");
  }

  @Override
  public boolean isCommandLineLoggingEnabled() {
    return false;
  }

  @Override
  List<String> getArguments() {
    return singletonList("--version");
  }
}
