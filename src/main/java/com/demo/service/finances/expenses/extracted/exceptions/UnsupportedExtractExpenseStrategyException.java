package com.demo.service.finances.expenses.extracted.exceptions;

import com.demo.commons.errors.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

public class UnsupportedExtractExpenseStrategyException extends GenericException {

  public static final String ERROR_CODE = "00.00.02";

  public UnsupportedExtractExpenseStrategyException(String from, String subject) {
    super(
        ERROR_CODE,
        "Unsupported extract expense strategy: " + from + ", " + subject,
        INTERNAL_SERVER_ERROR
    );
  }
}
