package io.snyk.plugins.teamcity.agent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.snyk.plugins.teamcity.agent.commands.SnykVersionCommand;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.TeamCityRuntimeException;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.agent.runner.CommandExecution;
import jetbrains.buildServer.agent.runner.CommandLineBuildService;
import jetbrains.buildServer.agent.runner.MultiCommandBuildSession;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;

public class SnykCommandBuildSession implements MultiCommandBuildSession {

  private final BuildRunnerContext buildRunnerContext;

  private Iterator<CommandExecutionAdapter> buildSteps;
  private CommandExecutionAdapter lastCommand;

  SnykCommandBuildSession(@NotNull BuildRunnerContext buildRunnerContext) {
    this.buildRunnerContext = requireNonNull(buildRunnerContext);
  }

  @Override
  public void sessionStarted() {
    buildSteps = getBuildSteps();
  }

  @Nullable
  @Override
  public CommandExecution getNextCommand() {
    if (buildSteps.hasNext()) {
      lastCommand = buildSteps.next();
      return lastCommand;
    }
    return null;
  }

  @Nullable
  @Override
  public BuildFinishedStatus sessionFinished() {
    return lastCommand.getResult();
  }

  private Iterator<CommandExecutionAdapter> getBuildSteps() {
    List<CommandExecutionAdapter> steps = new ArrayList<>(3);

    SnykVersionCommand snykVersionCommand = new SnykVersionCommand();
    steps.add(addCommand(snykVersionCommand));

    return steps.iterator();
  }

  private CommandExecutionAdapter addCommand(CommandLineBuildService buildService) {
    try {
      buildService.initialize(buildRunnerContext.getBuild(), buildRunnerContext);
    } catch (RunBuildException ex) {
      throw new TeamCityRuntimeException(ex);
    }
    return new CommandExecutionAdapter(buildService);
  }
}
