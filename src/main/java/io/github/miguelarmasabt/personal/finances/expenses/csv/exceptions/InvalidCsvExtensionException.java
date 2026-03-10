package io.github.miguelarmasabt.personal.finances.expenses.csv.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;

import static io.github.miguelarmasabt.commons.constants.Errors.INVALID_CSV_EXTENSION;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

public class InvalidCsvExtensionException extends GenericException {

  public InvalidCsvExtensionException() {
    super(INVALID_CSV_EXTENSION, BAD_REQUEST);
  }
}
