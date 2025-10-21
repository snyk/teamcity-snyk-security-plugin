package io.snyk.plugins.teamcity.agent.commands;

import static jetbrains.buildServer.util.PropertiesUtil.getBoolean;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants;
import jetbrains.buildServer.agent.runner.ProgramCommandLine;
import jetbrains.buildServer.agent.runner.SimpleProgramCommandLine;
import org.jetbrains.annotations.NotNull;

public class SnykReportCommand extends SnykBuildServiceAdapter {

  @NotNull
  @Override
  public ProgramCommandLine makeProgramCommandLine() {
    String reportMapperPath = getReportMapperPath();
    return new SimpleProgramCommandLine(getBuildParameters().getEnvironmentVariables(),
                                        getWorkingDirectory().getAbsolutePath(),
                                        reportMapperPath,
                                        getArguments());
  }

  @Override
  public void beforeProcessStarted() {
    getBuild().getBuildLogger().message("Generating Snyk html report...");
  }

  @Override
  List<String> getArguments() {
    List<String> arguments = new ArrayList<>();
  
    String buildTempDirectory = getBuild().getBuildTempDirectory().getAbsolutePath();
    
    // Determine which JSON file to use based on configuration
    String snykReportJson;
    String runSbomOnBuild = getRunnerParameters().get(SnykSecurityRunnerConstants.RUN_SBOM);
    if (getBoolean(runSbomOnBuild)) {
      snykReportJson = Paths.get(buildTempDirectory, SnykSecurityRunnerConstants.SNYK_SBOM_TEST_JSON_FILE).toFile().getAbsolutePath();
    } else {
      snykReportJson = Paths.get(buildTempDirectory, SnykSecurityRunnerConstants.SNYK_REPORT_JSON_FILE).toFile().getAbsolutePath();
    }
    
    String snykReportHtml = Paths.get(buildTempDirectory, SnykSecurityRunnerConstants.SNYK_REPORT_HTML_FILE).toFile().getAbsolutePath();
  
    arguments.add("-i");
    arguments.add(snykReportJson);
    arguments.add("-o");
    arguments.add(snykReportHtml);
  
    return arguments;
  }
}
