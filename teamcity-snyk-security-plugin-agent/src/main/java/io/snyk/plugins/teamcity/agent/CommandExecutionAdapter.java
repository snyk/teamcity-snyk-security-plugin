package io.snyk.plugins.teamcity.agent;

import java.io.File;
import java.util.List;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import jetbrains.buildServer.agent.runner.CommandExecution;
import jetbrains.buildServer.agent.runner.CommandLineBuildService;
import jetbrains.buildServer.agent.runner.ProcessListener;
import jetbrains.buildServer.agent.runner.ProgramCommandLine;
import jetbrains.buildServer.agent.runner.TerminationAction;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

public class CommandExecutionAdapter implements CommandExecution {

  private static final Logger LOG = Logger.getLogger(CommandExecutionAdapter.class);

  private CommandLineBuildService buildService;
  private List<ProcessListener> listeners;
  private BuildFinishedStatus result;

  CommandExecutionAdapter(@NotNull CommandLineBuildService buildService) {
    this.buildService = requireNonNull(buildService);
    listeners = buildService.getListeners();
  }

  @NotNull
  @Override
  public ProgramCommandLine makeProgramCommandLine() throws RunBuildException {
    return buildService.makeProgramCommandLine();
  }

  @Override
  public void beforeProcessStarted() throws RunBuildException {
    buildService.beforeProcessStarted();
  }

  @NotNull
  @Override
  public TerminationAction interruptRequested() {
    return buildService.interrupt();
  }

  @Override
  public boolean isCommandLineLoggingEnabled() {
    return buildService.isCommandLineLoggingEnabled();
  }

  @Override
  public void onStandardOutput(@NotNull String text) {
    listeners.forEach(processListener -> processListener.onStandardOutput(text));
  }

  @Override
  public void onErrorOutput(@NotNull String text) {
    listeners.forEach(processListener -> processListener.onErrorOutput(text));
  }

  @Override
  public void processStarted(@NotNull String programCommandLine, @NotNull File workingDirectory) {
    listeners.forEach(processListener -> processListener.processStarted(programCommandLine, workingDirectory));
  }

  @Override
  public void processFinished(int exitCode) {
    try {
      buildService.afterProcessFinished();

      buildService.getListeners().forEach(listener -> listener.processFinished(exitCode));
      result = buildService.getRunResult(exitCode);
      if (result == BuildFinishedStatus.FINISHED_SUCCESS) {
        buildService.afterProcessSuccessfullyFinished();
      }
    } catch (RunBuildException ex) {
      LOG.error(ex);
    }
  }

  @NotNull
  BuildFinishedStatus getResult() {
    return result;
  }
}
