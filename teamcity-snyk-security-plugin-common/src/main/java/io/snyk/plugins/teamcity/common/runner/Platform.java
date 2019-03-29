package io.snyk.plugins.teamcity.common.runner;

public enum Platform {
  LINUX("linux"),
  MAC_OS("macos"),
  WINDOWS("win.exe");

  private final String suffix;

  Platform(String suffix) {
    this.suffix = suffix;
  }

  public String getSuffix() {
    return suffix;
  }
}
