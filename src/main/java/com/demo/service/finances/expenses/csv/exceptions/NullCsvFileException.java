package com.demo.service.finances.expenses.csv.exceptions;

import com.demo.commons.error.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

public class NullCsvFileException extends GenericException {

  public static final String ERROR_CODE = "0013";

  public NullCsvFileException() {
    super(
        ERROR_CODE,
        "CSV file is required.",
        BAD_REQUEST
    );
  }
}
