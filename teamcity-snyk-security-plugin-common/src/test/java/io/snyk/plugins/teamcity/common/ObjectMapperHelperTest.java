package io.snyk.plugins.teamcity.common;

import io.snyk.plugins.teamcity.common.model.SnykTestStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.*;

class ObjectMapperHelperTest {

  private static File TEST_FIXTURES_DIRECTORY;

  @BeforeAll
  static void setupAll() throws Exception {
    URL testDirectoryUrl = ObjectMapperHelperTest.class.getClassLoader().getResource("./fixtures");
    TEST_FIXTURES_DIRECTORY = new File(requireNonNull(testDirectoryUrl).toURI());
  }

  @Test
  @DisplayName("unmarshall report with single vuln definition")
  void unmarshall_singleProject_withVulns() throws Exception {
    Path snykReportJsonForSingleProject = Paths.get(TEST_FIXTURES_DIRECTORY.getAbsolutePath(), "single-module-project", "snyk_report_with_vulns.json");

    Optional<SnykTestStatus> snykTestStatus = ObjectMapperHelper.unmarshall(snykReportJsonForSingleProject);

    assertTrue(snykTestStatus.isPresent());
    assertAll("single-module-project-with-vulns",
      () -> assertFalse(snykTestStatus.get().ok),
      () -> assertEquals(10, snykTestStatus.get().dependencyCount),
      () -> assertEquals(3, snykTestStatus.get().uniqueCount)
    );
  }

  @Test
  @DisplayName("unmarshall report with multi vuln definitions")
  void unmarshall_multiProject_withVulns() throws Exception {
    Path snykReportJsonForSingleProject = Paths.get(TEST_FIXTURES_DIRECTORY.getAbsolutePath(), "multi-module-project", "snyk_report_with_vulns.json");

    Optional<SnykTestStatus> snykTestStatus = ObjectMapperHelper.unmarshall(snykReportJsonForSingleProject);

    assertTrue(snykTestStatus.isPresent());
    assertAll("multi-module-project-with-vulns",
      () -> assertFalse(snykTestStatus.get().ok),
      () -> assertEquals(6, snykTestStatus.get().dependencyCount),
      () -> assertEquals(1, snykTestStatus.get().uniqueCount)
    );
  }
}
