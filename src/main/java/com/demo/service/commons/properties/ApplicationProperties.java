package com.demo.service.commons.properties;

import com.demo.commons.properties.ConfigurationBaseProperties;
import com.demo.service.commons.properties.features.FeatureProperties;
import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;

@StaticInitSafe
@ConfigMapping(prefix = "configuration")
public interface ApplicationProperties extends ConfigurationBaseProperties {

  FeatureProperties features();
}