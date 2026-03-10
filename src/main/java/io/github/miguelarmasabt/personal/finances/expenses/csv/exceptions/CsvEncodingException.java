package io.github.miguelarmasabt.personal.finances.expenses.csv.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

public class CsvEncodingException extends GenericException {

  public static final String CODE = "2000";

  public CsvEncodingException(Throwable exception) {
    super(CODE, INTERNAL_SERVER_ERROR, exception);
  }
}
