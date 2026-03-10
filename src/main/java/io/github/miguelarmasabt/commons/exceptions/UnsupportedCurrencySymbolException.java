package io.github.miguelarmasabt.commons.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;

import static io.github.miguelarmasabt.commons.constants.Errors.UNSUPPORTED_CURRENCY_SYMBOL;
import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

public class UnsupportedCurrencySymbolException extends GenericException {

  public UnsupportedCurrencySymbolException(String symbol) {
    super(UNSUPPORTED_CURRENCY_SYMBOL, INTERNAL_SERVER_ERROR, symbol);
  }
}
