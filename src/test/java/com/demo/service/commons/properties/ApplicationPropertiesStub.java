package com.demo.service.commons.properties;

import com.demo.commons.properties.ProjectType;
import com.demo.commons.properties.logging.LoggingTemplate;
import com.demo.commons.properties.restclient.RestClient;
import com.demo.service.commons.properties.features.FeatureProperties;
import com.demo.service.commons.properties.features.FeaturePropertiesStub;
import lombok.Data;

import java.util.Map;
import java.util.Optional;

@Data
public class ApplicationPropertiesStub implements ApplicationProperties {

  private FeaturePropertiesStub features;

  @Override
  public FeatureProperties features() {
    return this.features;
  }

  @Override
  public Optional<ProjectType> projectType() {
    return Optional.empty();
  }

  @Override
  public Optional<LoggingTemplate> logging() {
    return Optional.empty();
  }

  @Override
  public Map<String, String> errorMessages() {
    return Map.of();
  }

  @Override
  public Map<String, RestClient> restClients() {
    return Map.of();
  }
}