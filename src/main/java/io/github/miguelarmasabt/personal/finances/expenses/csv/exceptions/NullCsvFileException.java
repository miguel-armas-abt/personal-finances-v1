package io.github.miguelarmasabt.personal.finances.expenses.csv.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;

import static io.github.miguelarmasabt.commons.constants.Errors.NULL_CSV_FILE;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

public class NullCsvFileException extends GenericException {

  public NullCsvFileException() {
    super(NULL_CSV_FILE, BAD_REQUEST);
  }
}
