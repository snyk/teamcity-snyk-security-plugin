package io.snyk.plugins.teamcity.agent;

import io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants;
import jetbrains.buildServer.agent.AgentBuildRunnerInfo;
import jetbrains.buildServer.agent.BuildAgentConfiguration;
import jetbrains.buildServer.agent.runner.CommandLineBuildService;
import jetbrains.buildServer.agent.runner.CommandLineBuildServiceFactory;
import org.jetbrains.annotations.NotNull;

public class SnykSecurityBuildServiceFactory implements CommandLineBuildServiceFactory {

  public SnykSecurityBuildServiceFactory() {
    // initialized by spring
  }

  @NotNull
  @Override
  public CommandLineBuildService createService() {
    return new SnykSecurityRunnerBuildService();
  }

  @NotNull
  @Override
  public AgentBuildRunnerInfo getBuildRunnerInfo() {
    return new AgentBuildRunnerInfo() {
      @NotNull
      @Override
      public String getType() {
        return SnykSecurityRunnerConstants.RUNNER_TYPE;
      }

      @Override
      public boolean canRun(@NotNull BuildAgentConfiguration buildAgentConfiguration) {
        return true;
      }
    };
  }
}
