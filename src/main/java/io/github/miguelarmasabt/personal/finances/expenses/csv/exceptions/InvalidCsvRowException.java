package io.github.miguelarmasabt.personal.finances.expenses.csv.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

public class InvalidCsvRowException extends GenericException {

  public static final String ERROR_CODE = "0012";

  public InvalidCsvRowException(long rowIndex, int headersLength) {
    super(
        ERROR_CODE,
        "Row " + rowIndex + " doesn't contain " + headersLength + " columns.",
        BAD_REQUEST
    );
  }

  public InvalidCsvRowException(long rowIndex, String field) {
    super(
        ERROR_CODE,
        "Row " + rowIndex + " is missing required field '" + field + "'.",
        BAD_REQUEST
    );
  }
}
