package io.snyk.plugins.teamcity.server;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import jetbrains.buildServer.serverSide.SBuild;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.PositionConstraint;
import jetbrains.buildServer.web.openapi.ViewLogTab;
import org.jetbrains.annotations.NotNull;

public class SnykSecurityReportTab extends ViewLogTab {

  private static final String TAB_TITLE = "Snyk Security Report";
  private static final String TAB_CODE = "snykSecurityReport";

  public SnykSecurityReportTab(@NotNull PagePlaces pagePlaces, @NotNull SBuildServer server, @NotNull PluginDescriptor pluginDescriptor) {
    super(TAB_TITLE, TAB_CODE, pagePlaces, server);

    setIncludeUrl(pluginDescriptor.getPluginResourcesPath("tab/snykSecurityReport.jsp"));
    setPosition(PositionConstraint.after("artifacts"));
    addCssFile(pluginDescriptor.getPluginResourcesPath("tab/snykSecurityReport.css"));
  }

  @Override
  protected void fillModel(@NotNull Map<String, Object> map, @NotNull HttpServletRequest httpServletRequest, @NotNull SBuild build) {
    //TODO: fill model with link to the report
  }
}
