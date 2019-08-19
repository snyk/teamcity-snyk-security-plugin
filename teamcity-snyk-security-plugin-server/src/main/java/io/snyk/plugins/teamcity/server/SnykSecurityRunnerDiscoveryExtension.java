package io.snyk.plugins.teamcity.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jetbrains.buildServer.serverSide.discovery.BreadthFirstRunnerDiscoveryExtension;
import jetbrains.buildServer.serverSide.discovery.DiscoveredObject;
import jetbrains.buildServer.util.CollectionsUtil;
import jetbrains.buildServer.util.browser.Element;
import org.jetbrains.annotations.NotNull;

import static io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants.FAIL_ON_ISSUES;
import static io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants.MONITOR_PROJECT_ON_BUILD;
import static io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants.RUNNER_TYPE;
import static io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants.SEVERITY_THRESHOLD;

public class SnykSecurityRunnerDiscoveryExtension extends BreadthFirstRunnerDiscoveryExtension {

  private static final Set<String> SUPPORTED_FILES = CollectionsUtil.setOf("yarn.lock",
                                                                           "package-lock.json",
                                                                           "package.json",
                                                                           "Gemfile.lock",
                                                                           "pom.xml",
                                                                           "build.gradle",
                                                                           "build.sbt",
                                                                           "Pipfile",
                                                                           "requirements.txt",
                                                                           "Gopkg.lock",
                                                                           "vendor/vendor.json",
                                                                           "obj/project.assets.json",
                                                                           "packages.config",
                                                                           "composer.lock",
                                                                           "build.gradle.kts");

  @NotNull
  @Override
  protected List<DiscoveredObject> discoverRunnersInDirectory(@NotNull Element dir, @NotNull List<Element> filesAndDirs) {
    List<DiscoveredObject> runners = new ArrayList<>(1);
    boolean enableSnykSecurityBuildStep = filesAndDirs.stream().anyMatch(element -> element.isLeaf() && SUPPORTED_FILES.contains(element.getName()));

    if (enableSnykSecurityBuildStep) {
      Map<String, String> defaultProperties = new HashMap<>(3);
      defaultProperties.put(SEVERITY_THRESHOLD, "low");
      defaultProperties.put(FAIL_ON_ISSUES, "true");
      defaultProperties.put(MONITOR_PROJECT_ON_BUILD, "true");
      runners.add(new DiscoveredObject(RUNNER_TYPE, defaultProperties));
    }
    return runners;
  }
}
