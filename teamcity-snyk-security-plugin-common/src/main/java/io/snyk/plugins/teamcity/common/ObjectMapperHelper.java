package io.snyk.plugins.teamcity.common;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import io.snyk.plugins.teamcity.common.model.SnykTestStatus;

public final class ObjectMapperHelper {

  private static final JsonFactory JSON_FACTORY;

  static {
    JSON_FACTORY = new MappingJsonFactory();
  }

  private ObjectMapperHelper() {
  }

  public static Optional<SnykTestStatus> unmarshall(Path path) throws IOException {
    if (path == null) {
      return Optional.empty();
    }

    try (JsonParser parser = JSON_FACTORY.createParser(path.toFile())) {
      JsonToken token = parser.nextToken();
      if (token == JsonToken.START_ARRAY) {
        List<SnykTestStatus> testStatuses = new ArrayList<>();
        while (parser.nextToken() != JsonToken.END_ARRAY) {
          testStatuses.add(readTestResult(parser));
        }
        return Optional.of(aggregateTestResults(testStatuses));
      } else if (token == JsonToken.START_OBJECT) {
        return Optional.of(readTestResult(parser));
      } else {
        return Optional.empty();
      }
    }
  }

  private static SnykTestStatus readTestResult(JsonParser parser) throws IOException {
    SnykTestStatus snykTestStatus = new SnykTestStatus();

    while (parser.nextToken() != JsonToken.END_OBJECT) {
      String fieldName = parser.getCurrentName();

      if ("ok".equals(fieldName)) {
        parser.nextToken();
        snykTestStatus.ok = parser.getBooleanValue();
      } else if ("error".equals(fieldName)) {
        parser.nextToken();
        snykTestStatus.error = parser.getText();
      } else if ("uniqueCount".equals(fieldName)) {
        parser.nextToken();
        snykTestStatus.uniqueCount = parser.getIntValue();
      } else if ("dependencyCount".equals(fieldName)) {
        parser.nextToken();
        snykTestStatus.dependencyCount = parser.getIntValue();
      } else {
        parser.skipChildren();
      }
    }

    return snykTestStatus;
  }

  private static SnykTestStatus aggregateTestResults(List<SnykTestStatus> testStatuses) {
    SnykTestStatus aggregatedTestStatus = new SnykTestStatus();

    testStatuses.forEach(entity -> {
      aggregatedTestStatus.ok = aggregatedTestStatus.ok && entity.ok;
      aggregatedTestStatus.error = (entity.error != null && !entity.error.isEmpty())
        ? String.join(". ", aggregatedTestStatus.error, entity.error)
        : aggregatedTestStatus.error;
      aggregatedTestStatus.dependencyCount += entity.dependencyCount;
      aggregatedTestStatus.uniqueCount += entity.uniqueCount;
    });

    return aggregatedTestStatus;
  }
}
