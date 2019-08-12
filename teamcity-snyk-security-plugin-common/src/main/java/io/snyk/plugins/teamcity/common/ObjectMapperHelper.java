package io.snyk.plugins.teamcity.common;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import io.snyk.plugins.teamcity.common.model.SnykApiResponse;

public final class ObjectMapperHelper {

  private static final JsonFactory JSON_FACTORY;

  static {
    JSON_FACTORY = new MappingJsonFactory();
  }

  private ObjectMapperHelper() {
  }

  public static Optional<SnykApiResponse> unmarshall(Path path) throws IOException {
    if (path == null) {
      return Optional.empty();
    }

    try (JsonParser parser = JSON_FACTORY.createParser(path.toFile())) {
      if (parser.nextToken() != JsonToken.START_OBJECT) {
        return Optional.empty();
      }

      SnykApiResponse snykApiResponse = new SnykApiResponse();

      while (parser.nextToken() != JsonToken.END_OBJECT) {
        String fieldName = parser.getCurrentName();

        if ("summary".equals(fieldName)) {
          parser.nextToken();
          snykApiResponse.summary = parser.getText();
        } else if ("error".equals(fieldName)) {
          parser.nextToken();
          snykApiResponse.error = parser.getText();
        } else if ("uniqueCount".equals(fieldName)) {
          parser.nextToken();
          snykApiResponse.uniqueCount = parser.getIntValue();
        } else {
          parser.skipChildren();
        }
      }

      return Optional.of(snykApiResponse);
    }
  }
}
