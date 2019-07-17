package io.snyk.plugins.teamcity.common.runner;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import static io.snyk.plugins.teamcity.common.runner.Platform.LINUX;
import static io.snyk.plugins.teamcity.common.runner.Platform.MAC_OS;
import static io.snyk.plugins.teamcity.common.runner.Platform.WINDOWS;
import static java.util.Arrays.asList;

public final class Runners {

  private static final TreeMap<String, RunnerVersion> AVAILABLE_RUNNERS = new TreeMap<>();

  // all bundled versions should be initialized here
  static {
    AVAILABLE_RUNNERS.put("1.193.2", new RunnerVersion("1.193.2", new HashSet<>(asList(LINUX, MAC_OS, WINDOWS))) {
      @Override
      public String getSnykToolPath(Platform platform) {
        if (platform == null) {
          return "snyk-linux";
        }
        return "snyk-" + platform.getSuffix();
      }

      @Override
      public String getReportMapperPath(Platform platform) {
        if (platform == null) {
          return "snyk-to-html-linux";
        }
        return "snyk-to-html-" + platform.getSuffix();
      }
    });
  }

  public static RunnerVersion getRunner(String version) {
    return AVAILABLE_RUNNERS.get(version);
  }

  public Set<String> getVersions() {
    return AVAILABLE_RUNNERS.descendingKeySet();
  }
}
