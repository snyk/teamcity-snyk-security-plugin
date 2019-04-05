package io.snyk.plugins.teamcity.agent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.snyk.plugins.teamcity.agent.commands.SnykVersionCommand;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.agent.runner.CommandExecution;
import jetbrains.buildServer.agent.runner.MultiCommandBuildSession;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants.MONITOR_PROJECT_ON_BUILD;
import static java.util.Objects.requireNonNull;
import static org.apache.log4j.Logger.getLogger;

public class SnykCommandBuildSession implements MultiCommandBuildSession {

  private static final Logger LOG = getLogger(SnykCommandBuildSession.class);

  private final BuildRunnerContext buildRunnerContext;
  private Iterator<CommandExecution> buildSteps;

  SnykCommandBuildSession(@NotNull BuildRunnerContext buildRunnerContext) {
    this.buildRunnerContext = requireNonNull(buildRunnerContext);
  }

  @Override
  public void sessionStarted() throws RunBuildException {
    buildSteps = getSteps();
  }

  @Nullable
  @Override
  public CommandExecution getNextCommand() throws RunBuildException {
    if (buildSteps.hasNext()) {
      return buildSteps.next();
    }
    return null;
  }

  @Nullable
  @Override
  public BuildFinishedStatus sessionFinished() {
    return BuildFinishedStatus.FINISHED_SUCCESS;
  }

  private Iterator<CommandExecution> getSteps() {
    List<CommandExecution> steps = new ArrayList<>(3);

    CommandExecution snykVersionCommand = new SnykVersionCommand(buildRunnerContext);
    steps.add(snykVersionCommand);

    String monitorProjectOnBuild = buildRunnerContext.getRunnerParameters().get(MONITOR_PROJECT_ON_BUILD);
    LOG.error("monitorProjectOnBuild: " + monitorProjectOnBuild);

    return steps.iterator();
  }
}
