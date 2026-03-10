package io.github.miguelarmasabt.personal.finances.expenses.csv.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

public class InvalidCsvHeaderException extends GenericException {

  public InvalidCsvHeaderException(int index, String expectedHeader, String currentHeader) {
    super("2006", BAD_REQUEST, String.valueOf(index), expectedHeader, currentHeader);
  }

  public InvalidCsvHeaderException() {
    super("2007", BAD_REQUEST);
  }
}
