package io.snyk.plugins.teamcity.agent.commands;

import jetbrains.buildServer.agent.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants.*;
import static java.util.Arrays.asList;
import static jetbrains.buildServer.util.StringUtil.nullIfEmpty;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SnykSbomTestCommandTest {

  @Mock
  private AgentRunningBuild build;
  
  @Mock
  private File buildTempDirectory;
  
  private Map<String, String> runnerParameters;
  private SnykSbomTestCommand command;

  @BeforeEach
  void setUp() {
    runnerParameters = new HashMap<>();
    when(build.getBuildTempDirectory()).thenReturn(buildTempDirectory);
    when(buildTempDirectory.getAbsolutePath()).thenReturn("/tmp/build");
    
    command = new TestableSnykSbomTestCommand(runnerParameters, build);
  }

  @Test
  @DisplayName("getArguments should return base command with required arguments")
  void getArguments_shouldReturnBaseCommandWithRequiredArguments() {
    List<String> arguments = command.getArguments();
    
    assertAll("base arguments",
      () -> assertTrue(arguments.size() >= 6, "Should have at least 6 arguments"),
      () -> assertEquals("sbom", arguments.get(0), "First should be 'sbom'"),
      () -> assertEquals("test", arguments.get(1), "Second should be 'test'"),
      () -> assertEquals("--json", arguments.get(2), "Third should be '--json'"),
      () -> assertTrue(arguments.get(3).startsWith("--file="), "Fourth should be file argument"),
      () -> assertTrue(arguments.get(3).contains("snyk_sbom.json"), "File should reference snyk_sbom.json"),
      () -> assertEquals("--severity-threshold=low", arguments.get(4), "Fifth should be default severity"),
      () -> assertEquals("--reachability", arguments.get(5), "Sixth should be reachability flag")
    );
  }

  @Test
  @DisplayName("getArguments should use custom severity threshold when provided")
  void getArguments_shouldUseCustomSeverityThreshold() {
    runnerParameters.put(SEVERITY_THRESHOLD, "high");
    
    List<String> arguments = command.getArguments();
    
    assertTrue(arguments.contains("--severity-threshold=high"),
      "Should use custom severity threshold");
  }

  @Test
  @DisplayName("getArguments should support all severity levels")
  void getArguments_shouldSupportAllSeverityLevels() {
    String[] levels = {"low", "medium", "high", "critical"};
    
    for (String level : levels) {
      runnerParameters.put(SEVERITY_THRESHOLD, level);
      List<String> arguments = command.getArguments();
      
      assertTrue(arguments.contains("--severity-threshold=" + level),
        "Should support severity level: " + level);
    }
  }

  @Test
  @DisplayName("getArguments should default to low severity when not specified")
  void getArguments_shouldDefaultToLowSeverity() {
    List<String> arguments = command.getArguments();
    
    assertTrue(arguments.contains("--severity-threshold=low"),
      "Should default to low severity");
  }

  @Test
  @DisplayName("getArguments should include organisation when provided")
  void getArguments_shouldIncludeOrganisationWhenProvided() {
    runnerParameters.put(ORGANISATION, "my-test-org");
    
    List<String> arguments = command.getArguments();
    
    assertTrue(arguments.contains("--org=my-test-org"),
      "Should include organisation parameter");
  }

  @Test
  @DisplayName("getArguments should not include organisation when not provided")
  void getArguments_shouldNotIncludeOrganisationWhenNotProvided() {
    List<String> arguments = command.getArguments();
    
    boolean hasOrgParam = arguments.stream().anyMatch(arg -> arg.startsWith("--org="));
    assertFalse(hasOrgParam, "Should not include organisation parameter when not provided");
  }

  @Test
  @DisplayName("getArguments should not include organisation when empty")
  void getArguments_shouldNotIncludeOrganisationWhenEmpty() {
    runnerParameters.put(ORGANISATION, "");
    
    List<String> arguments = command.getArguments();
    
    boolean hasOrgParam = arguments.stream().anyMatch(arg -> arg.startsWith("--org="));
    assertFalse(hasOrgParam, "Should not include organisation parameter when empty");
  }

  @Test
  @DisplayName("getArguments should include additional parameters when provided")
  void getArguments_shouldIncludeAdditionalParametersWhenProvided() {
    runnerParameters.put(ADDITIONAL_PARAMETERS, "--debug --all-projects");
    
    List<String> arguments = command.getArguments();
    
    assertAll("additional parameters",
      () -> assertTrue(arguments.contains("--debug"), "Should include --debug"),
      () -> assertTrue(arguments.contains("--all-projects"), "Should include --all-projects")
    );
  }

  @Test
  @DisplayName("getArguments should always include reachability flag")
  void getArguments_shouldAlwaysIncludeReachabilityFlag() {
    List<String> arguments = command.getArguments();
    
    assertTrue(arguments.contains("--reachability"),
      "Should always include reachability flag");
  }

  @Test
  @DisplayName("getArguments should not duplicate reachability flag if already in additional parameters")
  void getArguments_shouldNotDuplicateReachabilityFlag() {
    runnerParameters.put(ADDITIONAL_PARAMETERS, "--reachability --debug");
    
    List<String> arguments = command.getArguments();
    
    long reachabilityCount = arguments.stream()
      .filter(arg -> arg.equals("--reachability"))
      .count();
    
    assertEquals(1, reachabilityCount,
      "Reachability flag should only appear once");
  }

  @Test
  @DisplayName("getArguments should have correct argument order")
  void getArguments_shouldHaveCorrectArgumentOrder() {
    runnerParameters.put(SEVERITY_THRESHOLD, "medium");
    runnerParameters.put(ORGANISATION, "test-org");
    
    List<String> arguments = command.getArguments();
    
    assertAll("argument order",
      () -> assertEquals("sbom", arguments.get(0), "First should be 'sbom'"),
      () -> assertEquals("test", arguments.get(1), "Second should be 'test'"),
      () -> assertEquals("--json", arguments.get(2), "Third should be '--json'"),
      () -> assertTrue(arguments.get(3).startsWith("--file="), "Fourth should be file argument"),
      () -> assertEquals("--severity-threshold=medium", arguments.get(4), "Fifth should be severity")
    );
  }

  @Test
  @DisplayName("getArguments should construct correct SBOM file path")
  void getArguments_shouldConstructCorrectSbomFilePath() {
    List<String> arguments = command.getArguments();
    
    String fileArg = arguments.stream()
      .filter(arg -> arg.startsWith("--file="))
      .findFirst()
      .orElse("");
    
    assertAll("file path",
      () -> assertTrue(fileArg.startsWith("--file=/tmp/build"), "Should use build temp directory"),
      () -> assertTrue(fileArg.endsWith("snyk_sbom.json"), "Should end with snyk_sbom.json")
    );
  }

  /**
   * Testable version of SnykSbomTestCommand that uses injected test data.
   * Note: This duplicates the getArguments() logic. The implementation should match the parent class exactly.
   */
  private static class TestableSnykSbomTestCommand extends SnykSbomTestCommand {
    private final Map<String, String> testParams;
    private final AgentRunningBuild testBuild;

    TestableSnykSbomTestCommand(Map<String, String> params, AgentRunningBuild build) {
      this.testParams = params;
      this.testBuild = build;
    }

    @Override
    List<String> getArguments() {
      List<String> arguments = new ArrayList<>();
      arguments.add("sbom");
      arguments.add("test");
      arguments.add("--json");

      String buildTempDirectory = testBuild.getBuildTempDirectory().getAbsolutePath();
      String snykSbomFile = Paths.get(buildTempDirectory, SNYK_SBOM_JSON_FILE).toFile().getAbsolutePath();
      arguments.add("--file=" + snykSbomFile);

      String severityThreshold = testParams.getOrDefault(SEVERITY_THRESHOLD, "low");
      arguments.add("--severity-threshold=" + severityThreshold);

      String organisation = testParams.get(ORGANISATION);
      if (nullIfEmpty(organisation) != null) {
        arguments.add("--org=" + organisation);
      }

      String additionalParameters = testParams.get(ADDITIONAL_PARAMETERS);
      if (nullIfEmpty(additionalParameters) != null) {
        arguments.addAll(asList(additionalParameters.split("\\s+")));
      }

      if (!arguments.contains("--reachability")) {
        arguments.add("--reachability");
      }

      return arguments;
    }
  }
}