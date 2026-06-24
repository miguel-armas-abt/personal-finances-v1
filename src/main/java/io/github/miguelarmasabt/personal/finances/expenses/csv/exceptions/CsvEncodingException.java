package io.github.miguelarmasabt.personal.finances.expenses.csv.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;

import static io.github.miguelarmasabt.commons.constants.Errors.INVALID_CSV_ROW;
import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

public class CsvEncodingException extends GenericException {

  public CsvEncodingException(Throwable exception) {
    super(INVALID_CSV_ROW, INTERNAL_SERVER_ERROR, exception);
  }
}
