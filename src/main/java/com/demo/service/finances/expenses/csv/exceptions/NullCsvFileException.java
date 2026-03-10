package com.demo.service.finances.expenses.csv.exceptions;

import com.demo.commons.errors.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

public class NullCsvFileException extends GenericException {

  public static final String ERROR_CODE = "00.00.01";

  public NullCsvFileException() {
    super(
        ERROR_CODE,
        "CSV file is required.",
        BAD_REQUEST
    );
  }
}
