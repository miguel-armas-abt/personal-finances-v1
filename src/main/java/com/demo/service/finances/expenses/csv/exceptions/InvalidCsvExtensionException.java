package com.demo.service.finances.expenses.csv.exceptions;

import com.demo.commons.errors.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

public class InvalidCsvExtensionException extends GenericException {

  public static final String ERROR_CODE = "00.00.01";

  public InvalidCsvExtensionException() {
    super(
        ERROR_CODE,
        "File extension must be .csv.",
        BAD_REQUEST
    );
  }
}
