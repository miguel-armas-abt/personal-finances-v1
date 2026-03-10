package io.github.miguelarmasabt.personal.finances.expenses.csv.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

public class MalformedCsvException extends GenericException {

  public static final String CODE = "2001";

  public MalformedCsvException(Throwable exception) {
    super(CODE, INTERNAL_SERVER_ERROR, exception);
  }
}
