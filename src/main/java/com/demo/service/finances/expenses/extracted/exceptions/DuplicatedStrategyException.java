package com.demo.service.finances.expenses.extracted.exceptions;

import com.demo.commons.errors.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

public class DuplicatedStrategyException extends GenericException {

  public static final String ERROR_CODE = "00.00.02";

  public DuplicatedStrategyException(String label) {
    super(
        ERROR_CODE,
        "Duplicate extract expense strategy detected: " + label,
        INTERNAL_SERVER_ERROR
    );
  }
}
