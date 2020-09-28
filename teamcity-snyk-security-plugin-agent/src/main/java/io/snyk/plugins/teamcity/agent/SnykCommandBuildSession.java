package io.snyk.plugins.teamcity.agent;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.snyk.plugins.teamcity.agent.commands.SnykBuildServiceAdapter;
import io.snyk.plugins.teamcity.agent.commands.SnykMonitorCommand;
import io.snyk.plugins.teamcity.agent.commands.SnykReportCommand;
import io.snyk.plugins.teamcity.agent.commands.SnykTestCommand;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.TeamCityRuntimeException;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher;
import jetbrains.buildServer.agent.runner.CommandExecution;
import jetbrains.buildServer.agent.runner.MultiCommandBuildSession;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants.MONITOR_PROJECT_ON_BUILD;
import static io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants.SNYK_ARTIFACTS_DIR;
import static io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants.SNYK_MONITOR_JSON_FILE;
import static io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants.SNYK_REPORT_HTML_FILE;
import static io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants.SNYK_REPORT_JSON_FILE;
import static java.io.File.separator;
import static java.util.Objects.requireNonNull;
import static jetbrains.buildServer.ArtifactsConstants.TEAMCITY_ARTIFACTS_DIR;
import static jetbrains.buildServer.util.PropertiesUtil.getBoolean;

public class SnykCommandBuildSession implements MultiCommandBuildSession {

  private final ArtifactsWatcher artifactsWatcher;
  private final BuildRunnerContext buildRunnerContext;

  private Iterator<CommandExecutionAdapter> buildSteps;
  private CommandExecutionAdapter lastCommand;

  SnykCommandBuildSession(@NotNull ArtifactsWatcher artifactsWatcher, @NotNull BuildRunnerContext buildRunnerContext) {
    this.artifactsWatcher = artifactsWatcher;
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
    String buildTempDirectory = buildRunnerContext.getBuild().getBuildTempDirectory().getAbsolutePath();
    Path snykHtmlReport = Paths.get(buildTempDirectory, SNYK_REPORT_HTML_FILE);
    Path snykJsonReport = Paths.get(buildTempDirectory, SNYK_REPORT_JSON_FILE);
    artifactsWatcher.addNewArtifactsPath(snykHtmlReport.toAbsolutePath().toString() + " => " + TEAMCITY_ARTIFACTS_DIR + separator + SNYK_ARTIFACTS_DIR);
    artifactsWatcher.addNewArtifactsPath(snykJsonReport.toAbsolutePath().toString() + " => " + TEAMCITY_ARTIFACTS_DIR + separator + SNYK_ARTIFACTS_DIR);

    return lastCommand.getResult();
  }

  private Iterator<CommandExecutionAdapter> getBuildSteps() {
    List<CommandExecutionAdapter> steps = new ArrayList<>(3);
    String buildTempDirectory = buildRunnerContext.getBuild().getBuildTempDirectory().getAbsolutePath();

    SnykTestCommand snykTestCommand = new SnykTestCommand();
    steps.add(addCommand(snykTestCommand, Paths.get(buildTempDirectory, SNYK_REPORT_JSON_FILE)));

    String monitorProjectOnBuild = buildRunnerContext.getRunnerParameters().get(MONITOR_PROJECT_ON_BUILD);
    if (getBoolean(monitorProjectOnBuild)) {
      SnykMonitorCommand snykMonitorCommand = new SnykMonitorCommand();
      steps.add(addCommand(snykMonitorCommand, Paths.get(buildTempDirectory, SNYK_MONITOR_JSON_FILE)));
    }

    SnykReportCommand snykReportCommand = new SnykReportCommand();
    steps.add(addCommand(snykReportCommand, Paths.get(buildTempDirectory, "report.output")));

    return steps.iterator();
  }

  private <T extends SnykBuildServiceAdapter> CommandExecutionAdapter addCommand(T buildService, Path commandOutputPath) {
    try {
      buildService.initialize(buildRunnerContext.getBuild(), buildRunnerContext);
    } catch (RunBuildException ex) {
      throw new TeamCityRuntimeException(ex);
    }
    return new CommandExecutionAdapter(buildService, commandOutputPath);
  }
}
