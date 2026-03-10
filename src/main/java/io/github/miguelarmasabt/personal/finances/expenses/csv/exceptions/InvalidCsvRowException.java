package io.github.miguelarmasabt.personal.finances.expenses.csv.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

public class InvalidCsvRowException extends GenericException {

  public InvalidCsvRowException(long rowIndex, int headersLength) {
    super(
        "2004",
        BAD_REQUEST,
        String.valueOf(rowIndex),
        String.valueOf(headersLength)
    );
  }

  public InvalidCsvRowException(long rowIndex, String field) {
    super(
        "2005",
        BAD_REQUEST,
        String.valueOf(rowIndex),
        field
    );
  }
}
