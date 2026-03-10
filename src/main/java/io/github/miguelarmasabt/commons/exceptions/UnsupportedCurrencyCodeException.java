package io.github.miguelarmasabt.commons.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

public class UnsupportedCurrencyCodeException extends GenericException {

  public static final String CODE = "1002";

  public UnsupportedCurrencyCodeException(String currencyCode) {
    super(CODE, INTERNAL_SERVER_ERROR, currencyCode);
  }
}
