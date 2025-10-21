package io.snyk.plugins.teamcity.agent.commands;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.runner.ProgramCommandLine;
import jetbrains.buildServer.agent.runner.SimpleProgramCommandLine;
import jetbrains.buildServer.util.FileUtil;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import static io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants.ADDITIONAL_PARAMETERS;
import static io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants.API_TOKEN;
import static io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants.CUSTOM_BUILD_TOOL_PATH;
import static io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants.ORGANISATION;
import static io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants.SNYK_INTEGRATION_NAME;
import static io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants.SEVERITY_THRESHOLD;
import static io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants.USE_CUSTOM_BUILD_TOOL_PATH;
import static java.lang.String.join;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static jetbrains.buildServer.util.PropertiesUtil.getBoolean;
import static jetbrains.buildServer.util.StringUtil.nullIfEmpty;

public class SnykSbomTestCommand extends SnykBuildServiceAdapter {

  private static final Logger LOG = Logger.getLogger(SnykSbomTestCommand.class);

  @NotNull
  @Override
  public ProgramCommandLine makeProgramCommandLine() throws RunBuildException {
    String snykToolPath = getSnykToolPath();

    String snykApiToken = getRunnerParameters().get(API_TOKEN);
    if (nullIfEmpty(snykApiToken) == null) {
      throw new RunBuildException("Snyk API token was not defined. Please configure the build properly and retry.");
    }
    Map<String, String> envVars = new HashMap<>(getEnvironmentVariables());
    envVars.put("SNYK_TOKEN", snykApiToken);
    envVars.put("SNYK_INTEGRATION_NAME", SNYK_INTEGRATION_NAME);
    configureCustomBuildPath(envVars);

    return new SimpleProgramCommandLine(envVars, getWorkingDirectory().getAbsolutePath(), snykToolPath, getArguments());
  }

  @Override
  public void beforeProcessStarted() {
    getBuild().getBuildLogger().message("Testing SBOM for known vulnerabilities...");
  }

  @Override
  List<String> getArguments() {
    List<String> arguments = new ArrayList<>();
    arguments.add("sbom");
    arguments.add("test");
    arguments.add("--json");

    // Get the SBOM file path from the build temp directory
    String buildTempDirectory = getBuild().getBuildTempDirectory().getAbsolutePath();
    String snykSbomFile = Paths.get(buildTempDirectory, SnykSecurityRunnerConstants.SNYK_SBOM_JSON_FILE).toFile().getAbsolutePath();
    arguments.add("--file=" + snykSbomFile);

    String severityThreshold = getRunnerParameters().getOrDefault(SEVERITY_THRESHOLD, "low");
    arguments.add("--severity-threshold=" + severityThreshold);

    String organisation = getRunnerParameters().get(ORGANISATION);
    if (nullIfEmpty(organisation) != null) {
      arguments.add("--org=" + organisation);
    }

    String additionalParameters = getRunnerParameters().get(ADDITIONAL_PARAMETERS);
    if (nullIfEmpty(additionalParameters) != null) {
      arguments.addAll(asList(additionalParameters.split("\\s+")));
    }

    // add "--reachability" argument if not already present
    // snyk-to-html only supports "sbom test" with reachability flag [https://github.com/snyk/snyk-to-html/pull/228]
    if (!arguments.contains("--reachability")) {
      arguments.add("--reachability");
    }

    return arguments;
  }

  private void configureCustomBuildPath(@NotNull Map<String, String> envVars) {
    String oldPath = envVars.get("PATH");
    String useCustomBuildToolPath = getRunnerParameters().get(USE_CUSTOM_BUILD_TOOL_PATH);
    String customBuildToolPath = getRunnerParameters().get(CUSTOM_BUILD_TOOL_PATH);

    if (getBoolean(useCustomBuildToolPath)) {
      // manual mode
      envVars.put("PATH", oldPath + File.pathSeparator + customBuildToolPath);
    } else {
      // auto-discover mode
      List<String> defaultToolPaths = getConfigParameters().entrySet().stream()
                                                           .filter(entry -> entry.getKey().startsWith("teamcity.tool."))
                                                           .filter(entry -> entry.getKey().contains("DEFAULT"))
                                                           .map(Map.Entry::getValue)
                                                           .collect(toList());
      List<String> toolsPath = new ArrayList<>(defaultToolPaths);
      defaultToolPaths.forEach(defaultToolPath -> {
        // should be refactored later, looks like a small overkill add every folder
        List<File> subDirectoriesInDefaultToolPath = FileUtil.getSubDirectories(Paths.get(defaultToolPath).toFile());
        subDirectoriesInDefaultToolPath.forEach(entry -> toolsPath.add(entry.getAbsolutePath()));
      });

      envVars.put("PATH", oldPath + File.pathSeparator + join(File.pathSeparator, toolsPath));
    }

    LOG.info("env.PATH for snyk build step: " + envVars.get("PATH"));
  }
}