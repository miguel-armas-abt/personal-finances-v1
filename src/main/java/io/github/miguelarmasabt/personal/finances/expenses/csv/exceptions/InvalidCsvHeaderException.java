package io.github.miguelarmasabt.personal.finances.expenses.csv.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;

import static io.github.miguelarmasabt.commons.constants.Errors.INVALID_CSV_HEADER_MSG01;
import static io.github.miguelarmasabt.commons.constants.Errors.INVALID_CSV_HEADER_MSG02;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

public class InvalidCsvHeaderException extends GenericException {

  public InvalidCsvHeaderException(int index, String expectedHeader, String currentHeader) {
    super(INVALID_CSV_HEADER_MSG01, BAD_REQUEST, String.valueOf(index), expectedHeader, currentHeader);
  }

  public InvalidCsvHeaderException() {
    super(INVALID_CSV_HEADER_MSG02, BAD_REQUEST);
  }
}
