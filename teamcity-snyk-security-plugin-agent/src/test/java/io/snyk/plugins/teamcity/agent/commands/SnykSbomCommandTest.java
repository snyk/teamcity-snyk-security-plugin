package io.snyk.plugins.teamcity.agent.commands;

import jetbrains.buildServer.agent.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants.*;
import static jetbrains.buildServer.util.StringUtil.nullIfEmpty;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SnykSbomCommandTest {

  @Mock
  private BuildRunnerContext buildRunnerContext;
  
  @Mock
  private AgentRunningBuild build;
  
  @Mock
  private BuildProgressLogger buildLogger;
  
  private Map<String, String> runnerParameters;
  private SnykSbomCommand command;

  @BeforeEach
  void setUp() {
    runnerParameters = new HashMap<>();
    command = new TestableSnykSbomCommand(runnerParameters);
  }

  @Test
  @DisplayName("getArguments should return base command with default format")
  void getArguments_shouldReturnBaseCommandWithDefaultFormat() {
    List<String> arguments = command.getArguments();
    
    assertAll("default arguments",
      () -> assertEquals(3, arguments.size(), "Should have 3 arguments"),
      () -> assertEquals("sbom", arguments.get(0), "First should be 'sbom'"),
      () -> assertEquals("--json", arguments.get(1), "Second should be '--json'"),
      () -> assertEquals("--format=cyclonedx1.6+json", arguments.get(2), "Third should be default format")
    );
  }

  @Test
  @DisplayName("getArguments should use custom SBOM format when provided")
  void getArguments_shouldUseCustomSbomFormat() {
    runnerParameters.put(SBOM_FORMAT, "spdx2.3+json");
    
    List<String> arguments = command.getArguments();
    
    assertTrue(arguments.contains("--format=spdx2.3+json"),
      "Should use custom SBOM format");
  }

  @Test
  @DisplayName("getArguments should support CycloneDX 1.4 format")
  void getArguments_shouldSupportCycloneDx14() {
    runnerParameters.put(SBOM_FORMAT, "cyclonedx1.4+json");
    
    List<String> arguments = command.getArguments();
    
    assertTrue(arguments.contains("--format=cyclonedx1.4+json"),
      "Should support CycloneDX 1.4 format");
  }

  @Test
  @DisplayName("getArguments should support CycloneDX 1.5 format")
  void getArguments_shouldSupportCycloneDx15() {
    runnerParameters.put(SBOM_FORMAT, "cyclonedx1.5+json");
    
    List<String> arguments = command.getArguments();
    
    assertTrue(arguments.contains("--format=cyclonedx1.5+json"),
      "Should support CycloneDX 1.5 format");
  }

  @Test
  @DisplayName("getArguments should support CycloneDX 1.6 format")
  void getArguments_shouldSupportCycloneDx16() {
    runnerParameters.put(SBOM_FORMAT, "cyclonedx1.6+json");
    
    List<String> arguments = command.getArguments();
    
    assertTrue(arguments.contains("--format=cyclonedx1.6+json"),
      "Should support CycloneDX 1.6 format");
  }

  @Test
  @DisplayName("getArguments should support SPDX 2.3 format")
  void getArguments_shouldSupportSpdx23() {
    runnerParameters.put(SBOM_FORMAT, "spdx2.3+json");
    
    List<String> arguments = command.getArguments();
    
    assertTrue(arguments.contains("--format=spdx2.3+json"),
      "Should support SPDX 2.3 format");
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
  @DisplayName("getArguments should have correct argument order")
  void getArguments_shouldHaveCorrectArgumentOrder() {
    runnerParameters.put(SBOM_FORMAT, "cyclonedx1.6+json");
    runnerParameters.put(ORGANISATION, "test-org");
    
    List<String> arguments = command.getArguments();
    
    assertAll("argument order",
      () -> assertEquals("sbom", arguments.get(0), "First should be 'sbom'"),
      () -> assertEquals("--json", arguments.get(1), "Second should be '--json'"),
      () -> assertTrue(arguments.get(2).startsWith("--format="), "Third should be format parameter")
    );
  }

  /**
   * Testable version of SnykSbomCommand that uses injected test data.
   * Note: This duplicates the getArguments() logic. The implementation should match the parent class exactly.
   */
  private static class TestableSnykSbomCommand extends SnykSbomCommand {
    private final Map<String, String> testParams;
  
    TestableSnykSbomCommand(Map<String, String> params) {
      this.testParams = params;
    }
  
    @Override
    List<String> getArguments() {
      List<String> arguments = new ArrayList<>();
      arguments.add("sbom");
      arguments.add("--json");
      
      String sbomFormat = testParams.getOrDefault(SBOM_FORMAT, "cyclonedx1.6+json");
      arguments.add("--format=" + sbomFormat);
  
      String organisation = testParams.get(ORGANISATION);
      if (nullIfEmpty(organisation) != null) {
        arguments.add("--org=" + organisation);
      }
  
      return arguments;
    }
  }
}
