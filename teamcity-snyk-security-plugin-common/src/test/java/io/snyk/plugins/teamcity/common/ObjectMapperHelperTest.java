package io.snyk.plugins.teamcity.common;

import io.snyk.plugins.teamcity.common.model.SnykApiResponse;
import org.junit.jupiter.api.BeforeAll;
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
  void unmarshall_singleProject() throws Exception {
    Path snykReportJsonForSingleProject = Paths.get(TEST_FIXTURES_DIRECTORY.getAbsolutePath(), "single-module-project", "snyk_report.json");

    Optional<SnykApiResponse> snykApiResponse = ObjectMapperHelper.unmarshall(snykReportJsonForSingleProject);

    assertTrue(snykApiResponse.isPresent());
    assertAll("single-module-project",
      () -> assertFalse(snykApiResponse.get().success),
      () -> assertEquals(3, snykApiResponse.get().uniqueCount),
      () -> assertEquals("3 vulnerable dependency paths", snykApiResponse.get().summary)
    );
  }
}
