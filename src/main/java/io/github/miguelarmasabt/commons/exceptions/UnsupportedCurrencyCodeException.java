package io.github.miguelarmasabt.commons.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

public class UnsupportedCurrencyCodeException extends GenericException {

  public static final String ERROR_CODE = "0001";

  public UnsupportedCurrencyCodeException(String code) {
    super(
        ERROR_CODE,
        "Unsupported currency code: " + code,
        INTERNAL_SERVER_ERROR
    );
  }
}
