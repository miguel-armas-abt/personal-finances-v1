package io.github.miguelarmasabt.commons.properties;

import io.github.miguelarmasabt.commons.properties.features.FeatureProperties;
import io.github.miguelarmasabt.properties.ConfigurationBaseProperties;
import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;

@StaticInitSafe
@ConfigMapping(prefix = "configuration")
public interface ApplicationProperties extends ConfigurationBaseProperties {

  FeatureProperties features();
}