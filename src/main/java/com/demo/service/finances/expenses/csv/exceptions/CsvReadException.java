package com.demo.service.finances.expenses.csv.exceptions;

import com.demo.commons.error.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

public class CsvReadException extends GenericException {

  public static final String ERROR_CODE = "0009";

  public CsvReadException(Throwable exception) {
    super(
        ERROR_CODE,
        "Could not read CSV.",
        INTERNAL_SERVER_ERROR,
        exception
    );
  }
}
