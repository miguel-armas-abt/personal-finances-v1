package io.github.miguelarmasabt.tools;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class JsonSerializer {

  private final ObjectMapper MAPPER;

  public JsonSerializer(ObjectMapper objectMapper) {
    this.MAPPER = objectMapper;
    this.MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
  }

  protected static InputStream getResourcesAsStream(String filePath) {
    String normalizedPath = filePath.startsWith("/") ? filePath.substring(1) : filePath;
    return Thread.currentThread().getContextClassLoader().getResourceAsStream(normalizedPath);
  }

  protected <T> T readValue(InputStream inputStream, Class<T> objectClass) {
    try (InputStream is = inputStream) {
      return MAPPER.readValue(is, objectClass);
    } catch (IOException ex) {
      throw new IllegalArgumentException(ex);
    }
  }

  protected <T> List<T> readList(InputStream inputStream, Class<T> objectClass) {
    try (InputStream is = inputStream) {
      CollectionType collectionType = MAPPER.getTypeFactory().constructCollectionType(List.class, objectClass);
      return MAPPER.readValue(is, collectionType);
    } catch (IOException ex) {
      throw new IllegalArgumentException(ex);
    }
  }

  public <T> T readElementFromFile(String filePath, Class<T> objectClass) {
    return readValue(getResourcesAsStream(filePath), objectClass);
  }

  public <T> List<T> readListFromFile(String filePath, Class<T> objectClass) {
    return readList(getResourcesAsStream(filePath), objectClass);
  }
}