package com.demo.service.finances.expenses.csv.exceptions;

import com.demo.commons.errors.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

public class CsvReadException extends GenericException {

  public static final String ERROR_CODE = "00.00.01";

  public CsvReadException(Throwable exception) {
    super(
        ERROR_CODE,
        exception,
        "Could not read CSV.",
        INTERNAL_SERVER_ERROR
    );
  }
}
