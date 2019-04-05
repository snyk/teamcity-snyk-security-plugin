package io.snyk.plugins.teamcity.agent;

import io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.AgentBuildRunnerInfo;
import jetbrains.buildServer.agent.BuildAgentConfiguration;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.agent.runner.MultiCommandBuildSession;
import jetbrains.buildServer.agent.runner.MultiCommandBuildSessionFactory;
import org.jetbrains.annotations.NotNull;

public class SnykBuildSessionFactory implements MultiCommandBuildSessionFactory {

  public SnykBuildSessionFactory() {
    // initialized by spring
  }

  @NotNull
  @Override
  public MultiCommandBuildSession createSession(@NotNull BuildRunnerContext runnerContext) throws RunBuildException {
    return new SnykCommandBuildSession(runnerContext);
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
