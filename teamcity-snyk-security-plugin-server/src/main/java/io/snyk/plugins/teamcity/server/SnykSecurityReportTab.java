package io.snyk.plugins.teamcity.server;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants;
import jetbrains.buildServer.serverSide.SBuild;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.artifacts.BuildArtifact;
import jetbrains.buildServer.serverSide.artifacts.BuildArtifactsViewMode;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.PositionConstraint;
import jetbrains.buildServer.web.openapi.ViewLogTab;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import static io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants.SNYK_ARTIFACTS_DIR;
import static io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants.SNYK_REPORT_HTML_FILE;
import static java.io.File.separator;
import static jetbrains.buildServer.ArtifactsConstants.TEAMCITY_ARTIFACTS_DIR;

public class SnykSecurityReportTab extends ViewLogTab {

  private static final String TAB_TITLE = "Snyk Security Report";
  private static final String TAB_CODE = "snykSecurityReport";

  private static final Logger LOG = Logger.getLogger(SnykSecurityReportTab.class);

  public SnykSecurityReportTab(@NotNull PagePlaces pagePlaces, @NotNull SBuildServer server, @NotNull PluginDescriptor pluginDescriptor) {
    super(TAB_TITLE, TAB_CODE, pagePlaces, server);

    String path = pluginDescriptor.getPluginResourcesPath("tab/snykReport.jsp");
    setIncludeUrl(path);
    setPosition(PositionConstraint.after("artifacts"));
  }

  @Override
  protected void fillModel(@NotNull Map<String, Object> map, @NotNull HttpServletRequest httpServletRequest, @NotNull SBuild build) {
    map.put("snykHtmlReportContent", readSnykHtmlReport(build));
  }

  @Override
  protected boolean isAvailable(@NotNull HttpServletRequest request, @NotNull SBuild build) {
    SBuildType buildType = build.getBuildType();
    if (buildType == null || !build.isFinished()) {
      return false;
    }
    return buildType.getRunnerTypes().contains(SnykSecurityRunnerConstants.RUNNER_TYPE);
  }

  private String readSnykHtmlReport(@NotNull SBuild build) {
    String snykHtmlReportPath = TEAMCITY_ARTIFACTS_DIR + separator + SNYK_ARTIFACTS_DIR + separator + SNYK_REPORT_HTML_FILE;
    BuildArtifact artifact = build.getArtifacts(BuildArtifactsViewMode.VIEW_HIDDEN_ONLY).getArtifact(snykHtmlReportPath);

    if (artifact == null) {
      LOG.error("An error occurred while reading the Snyk HTML report - artifact not found");
      return generateErrorMessage("Artifact not found");
    }

    try (InputStream inputStream = artifact.getInputStream();
         BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
      StringBuilder content = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        content.append(line).append("\n");
      }
      return content.toString();
    } catch (IOException e) {
      LOG.error("An error occurred while reading the Snyk HTML report.", e);
      return generateErrorMessage("An error occurred while reading the report");
    }
  }

  private String generateErrorMessage(String message) {
    return "<html><head><title>Error</title></head><body><h1>Snyk report error: " + message + "</h1></body></html>";
  }
}
