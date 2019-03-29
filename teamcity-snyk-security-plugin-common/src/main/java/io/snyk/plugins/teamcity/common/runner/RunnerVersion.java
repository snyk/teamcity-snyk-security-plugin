package io.snyk.plugins.teamcity.common.runner;

import java.util.Set;

import static java.util.Objects.requireNonNull;

public abstract class RunnerVersion {
  private final String version;
  private final Set<Platform> platforms;

  RunnerVersion(String version, Set<Platform> platforms) {
    this.version = requireNonNull(version);
    this.platforms = requireNonNull(platforms);
  }

  /**
   * Returns the path to <code>snyk</code> CLI binary file
   */
  public abstract String getSnykToolPath(Platform platform);

  /**
   * Returns the path to <code>snyk-to-html</code> binary file
   */
  public abstract String getReportMapperPath(Platform platform);
}
