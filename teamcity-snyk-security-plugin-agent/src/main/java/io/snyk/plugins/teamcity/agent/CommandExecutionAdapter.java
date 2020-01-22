package io.snyk.plugins.teamcity.agent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import io.snyk.plugins.teamcity.agent.commands.SnykBuildServiceAdapter;
import io.snyk.plugins.teamcity.common.ObjectMapperHelper;
import jetbrains.buildServer.BuildProblemData;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.TeamCityRuntimeException;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import jetbrains.buildServer.agent.runner.CommandExecution;
import jetbrains.buildServer.agent.runner.ProcessListener;
import jetbrains.buildServer.agent.runner.ProgramCommandLine;
import jetbrains.buildServer.agent.runner.TerminationAction;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import static io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants.FAIL_ON_ISSUES;
import static io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants.RUNNER_DISPLAY_NAME;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.util.Objects.requireNonNull;
import static jetbrains.buildServer.BuildProblemTypes.TC_ERROR_MESSAGE_TYPE;
import static jetbrains.buildServer.util.PropertiesUtil.getBoolean;
import static jetbrains.buildServer.util.StringUtil.nullIfEmpty;

public class CommandExecutionAdapter implements CommandExecution {

  private static final Logger LOG = Logger.getLogger(CommandExecutionAdapter.class);

  private final SnykBuildServiceAdapter buildService;
  private final Path commandOutputPath;
  private List<ProcessListener> listeners;
  private BuildFinishedStatus result;

  CommandExecutionAdapter(@NotNull SnykBuildServiceAdapter buildService, @NotNull Path commandOutputPath) {
    this.buildService = requireNonNull(buildService);
    this.commandOutputPath = requireNonNull(commandOutputPath);
    listeners = buildService.getListeners();
  }

  @NotNull
  BuildFinishedStatus getResult() {
    return result;
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
    if (nullIfEmpty(text) == null) {
      return;
    }

    try {
      Files.write(commandOutputPath, text.getBytes(UTF_8), CREATE, APPEND);
    } catch (IOException ex) {
      throw new TeamCityRuntimeException(format("Could not write output into '%s'", commandOutputPath.toString()), ex);
    }
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
      listeners.forEach(processListener -> processListener.processFinished(exitCode));
      result = buildService.getRunResult(exitCode);
      if (result == BuildFinishedStatus.FINISHED_SUCCESS) {
        buildService.afterProcessSuccessfullyFinished();
      }

      if (exitCode != 0) {
        ObjectMapperHelper.unmarshall(commandOutputPath).ifPresent(snykTestStatus -> {
          // "error" indicates a hard error, so declare the build as failed
          if (nullIfEmpty(snykTestStatus.error) != null) {
            BuildProblemData buildProblem = createBuildProblem(snykTestStatus.error);
            buildService.getLogger().logBuildProblem(buildProblem);
            result = BuildFinishedStatus.FINISHED_FAILED;
          }

          if (!snykTestStatus.ok) {
            String problem = format("%s known vulnerabilities | %s dependencies", snykTestStatus.uniqueCount, snykTestStatus.dependencyCount);

            /*
             * we check whether 'failOnIssues' runner parameter exists in case of old configurations.
             * if 'failOnIssues' was not found, then treat it as 'true' so fail behavior of the plugin is unchanged.
             */
            boolean containsFailOnIssues = buildService.getBuildRunnerContext().getRunnerParameters().containsKey(FAIL_ON_ISSUES);
            String failOnIssues = buildService.getBuildRunnerContext().getRunnerParameters().get(FAIL_ON_ISSUES);
            if (getBoolean(failOnIssues) || !containsFailOnIssues) {
              BuildProblemData buildProblem = createBuildProblem(problem);
              buildService.getLogger().logBuildProblem(buildProblem);
            } else {
              buildService.getLogger().error(problem);
            }
          }
        });
      }
    } catch (RunBuildException | IOException ex) {
      buildService.getLogger().warning(ex.getMessage());
      LOG.error(ex);
    }
  }

  private BuildProblemData createBuildProblem(@NotNull String description) {
    String error = format("%s (Step: %s)", description, RUNNER_DISPLAY_NAME);
    return BuildProblemData.createBuildProblem(valueOf(description.hashCode()), TC_ERROR_MESSAGE_TYPE, error);
  }
}
