package com.demo.service.expenses.extracted.exceptions;

import com.demo.commons.error.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

public class DuplicatedStrategyException extends GenericException {

  public static final String ERROR_CODE = "0014";

  public DuplicatedStrategyException(String label) {
    super(
        ERROR_CODE,
        "Duplicate extract expense strategy detected: " + label,
        INTERNAL_SERVER_ERROR
    );
  }
}
