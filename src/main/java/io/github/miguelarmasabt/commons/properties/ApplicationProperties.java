package io.github.miguelarmasabt.commons.properties;

import io.github.miguelarmasabt.commons.properties.business.BusinessProperties;
import io.github.miguelarmasabt.commons.properties.technical.TechnicalProperties;
import io.github.miguelarmasabt.properties.ConfigurationBaseProperties;
import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;

@StaticInitSafe
@ConfigMapping(prefix = "configuration")
public interface ApplicationProperties extends ConfigurationBaseProperties {

  BusinessProperties business();

  TechnicalProperties technical();
}