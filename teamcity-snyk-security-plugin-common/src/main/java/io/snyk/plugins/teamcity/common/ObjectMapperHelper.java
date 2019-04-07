package io.snyk.plugins.teamcity.common;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class ObjectMapperHelper {

  private static final ObjectMapper OBJECT_MAPPER;

  static {
    OBJECT_MAPPER = new ObjectMapper();
    OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  private ObjectMapperHelper() {
  }

  public static <T> Optional<T> unmarshall(String value, Class<T> clazz) throws IOException {
    if (value != null && value.length() > 0) {
      T result = OBJECT_MAPPER.readerFor(clazz).readValue(value.getBytes(StandardCharsets.UTF_8));
      return Optional.ofNullable(result);
    } else {
      return Optional.empty();
    }
  }
}
