package io.github.miguelarmasabt.commons.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;

import static io.github.miguelarmasabt.commons.constants.Errors.UNSUPPORTED_CURRENCY;
import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

public class UnsupportedCurrencyCodeException extends GenericException {

  public UnsupportedCurrencyCodeException(String currencyCode) {
    super(UNSUPPORTED_CURRENCY, INTERNAL_SERVER_ERROR, currencyCode);
  }
}
