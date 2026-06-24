package io.github.miguelarmasabt.personal.finances.expenses.csv.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;

import static io.github.miguelarmasabt.commons.constants.Errors.MALFORMED_CSV;
import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

public class MalformedCsvException extends GenericException {

  public static final String ERROR_CODE = "0009";

  public MalformedCsvException(Throwable exception) {
    super(MALFORMED_CSV, INTERNAL_SERVER_ERROR, exception);
  }
}
