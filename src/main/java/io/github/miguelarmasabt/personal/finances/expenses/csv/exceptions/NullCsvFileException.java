package io.github.miguelarmasabt.personal.finances.expenses.csv.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

public class NullCsvFileException extends GenericException {

  public static final String CODE = "2003";

  public NullCsvFileException() {
    super(CODE, BAD_REQUEST);
  }
}
