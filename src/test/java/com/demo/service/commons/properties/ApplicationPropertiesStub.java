package com.demo.service.commons.properties;

import com.demo.commons.properties.ProjectType;
import com.demo.commons.properties.error.ErrorProperties;
import com.demo.commons.properties.rest.RestProperties;
import com.demo.service.commons.properties.features.FeatureProperties;
import com.demo.service.commons.properties.features.FeaturePropertiesStub;
import lombok.Data;

@Data
public class ApplicationPropertiesStub implements ApplicationProperties {

  private FeaturePropertiesStub features;

  @Override
  public FeatureProperties features() {
    return this.features;
  }

  @Override
  public ProjectType projectType() {
    return ProjectType.BACKEND;
  }

  @Override
  public ErrorProperties error() {
    return null;
  }

  @Override
  public RestProperties rest() {
    return null;
  }
}