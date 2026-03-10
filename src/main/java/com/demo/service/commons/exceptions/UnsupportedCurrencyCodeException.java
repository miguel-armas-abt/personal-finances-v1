package com.demo.service.commons.exceptions;

import com.demo.commons.errors.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

public class UnsupportedCurrencyCodeException extends GenericException {

  public static final String ERROR_CODE = "00.00.01";

  public UnsupportedCurrencyCodeException(String code) {
    super(
        ERROR_CODE,
        "Unsupported currency code: " + code,
        INTERNAL_SERVER_ERROR
    );
  }
}
