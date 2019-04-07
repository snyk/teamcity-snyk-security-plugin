package io.snyk.plugins.teamcity.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SnykApiResponse {

  @JsonProperty("ok")
  public boolean success;

  @JsonProperty("error")
  public String error;

  @JsonProperty("summary")
  public String summary;
}
