package io.github.miguelarmasabt.personal.finances.expenses.csv.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;

import static io.github.miguelarmasabt.commons.constants.Errors.INVALID_CSV_ROW_MSG01;
import static io.github.miguelarmasabt.commons.constants.Errors.INVALID_CSV_ROW_MSG02;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

public class InvalidCsvRowException extends GenericException {

  public InvalidCsvRowException(long rowIndex, int headersLength) {
    super(
        INVALID_CSV_ROW_MSG01,
        BAD_REQUEST,
        String.valueOf(rowIndex),
        String.valueOf(headersLength)
    );
  }

  public InvalidCsvRowException(long rowIndex, String field) {
    super(
        INVALID_CSV_ROW_MSG02,
        BAD_REQUEST,
        String.valueOf(rowIndex),
        field
    );
  }
}
