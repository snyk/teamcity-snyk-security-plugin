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
  private static final String DEFAULT_VERSION = "1.1064.0";

  // all bundled versions should be initialized here
  static {
    // TODO additional architectures should be added here when needed, e.g. arm64 & alpine
    AVAILABLE_RUNNERS.put(DEFAULT_VERSION, new RunnerVersion(DEFAULT_VERSION, new HashSet<>(asList(LINUX, MAC_OS, WINDOWS))) {
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

  public static RunnerVersion getDefaultRunner() {
    return AVAILABLE_RUNNERS.get(DEFAULT_VERSION);
  }

  public static String getDefaultRunnerVersion() {
    return DEFAULT_VERSION;
  }

  public Set<String> getVersions() {
    return AVAILABLE_RUNNERS.descendingKeySet();
  }
}
