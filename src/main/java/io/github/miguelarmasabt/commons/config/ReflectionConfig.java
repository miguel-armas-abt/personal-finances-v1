package io.github.miguelarmasabt.commons.config;

import io.quarkus.runtime.annotations.RegisterForReflection;

//POJOs not referenced by JPA entities or JAX-RS endpoints
@RegisterForReflection(targets = {

})
public class ReflectionConfig {
}
