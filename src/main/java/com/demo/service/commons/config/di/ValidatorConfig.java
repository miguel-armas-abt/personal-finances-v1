package com.demo.service.commons.config.di;

import com.demo.commons.validations.BodyValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.validation.Validator;

@ApplicationScoped
public class ValidatorConfig {

  @Produces
  public BodyValidator requestValidator(Validator validator) {
    return new BodyValidator(validator);
  }
}