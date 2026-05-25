package com.demo.service.finances.expenses.csv.exceptions;

import com.demo.commons.error.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

public class InvalidCsvHeaderException extends GenericException {

  public static final String ERROR_CODE = "0011";

  public InvalidCsvHeaderException(int index, String expectedHeader, String currentHeader) {
    super(
        ERROR_CODE,
        "Invalid header at position " + index + ". Expected '" + expectedHeader + "' but got '" + currentHeader + "'.",
        BAD_REQUEST
    );
  }

  public InvalidCsvHeaderException() {
    super(
        ERROR_CODE,
        "CSV headers do not match expected structure.",
        BAD_REQUEST
    );
  }
}
