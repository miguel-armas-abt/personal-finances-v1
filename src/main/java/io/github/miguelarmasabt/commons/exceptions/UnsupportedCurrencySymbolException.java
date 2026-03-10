package io.github.miguelarmasabt.commons.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

public class UnsupportedCurrencySymbolException extends GenericException {

  public static final String CODE = "1003";

  public UnsupportedCurrencySymbolException(String symbol) {
    super(CODE, INTERNAL_SERVER_ERROR, symbol);
  }
}
