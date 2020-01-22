package io.snyk.plugins.teamcity.common.model;

public class SnykTestStatus {
  public boolean ok = true;
  public String error;
  public int dependencyCount;
  public int uniqueCount;
}
