package io.github.miguelarmasabt.commons.properties;

import io.github.miguelarmasabt.properties.ProjectType;
import io.github.miguelarmasabt.properties.error.ErrorProperties;
import io.github.miguelarmasabt.properties.rest.RestProperties;
import io.github.miguelarmasabt.commons.properties.features.FeatureProperties;
import io.github.miguelarmasabt.commons.properties.features.FeaturePropertiesStub;
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