package com.demo.service.commons.exceptions;

import com.demo.commons.error.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

public class UnsupportedCurrencySymbolException extends GenericException {

  public static final String ERROR_CODE = "0002";

  public UnsupportedCurrencySymbolException(String symbol) {
    super(
        ERROR_CODE,
        "Unsupported currency symbol: " + symbol,
        INTERNAL_SERVER_ERROR
    );
  }
}
