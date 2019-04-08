package io.snyk.plugins.teamcity.agent;

import io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants;
import jetbrains.buildServer.agent.AgentBuildRunnerInfo;
import jetbrains.buildServer.agent.BuildAgentConfiguration;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher;
import jetbrains.buildServer.agent.runner.MultiCommandBuildSession;
import jetbrains.buildServer.agent.runner.MultiCommandBuildSessionFactory;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

public class SnykBuildSessionFactory implements MultiCommandBuildSessionFactory {

  private final ArtifactsWatcher artifactsWatcher;

  public SnykBuildSessionFactory(@NotNull ArtifactsWatcher artifactsWatcher) {
    this.artifactsWatcher = requireNonNull(artifactsWatcher);
  }

  @NotNull
  @Override
  public MultiCommandBuildSession createSession(@NotNull BuildRunnerContext runnerContext) {
    return new SnykCommandBuildSession(artifactsWatcher, runnerContext);
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
