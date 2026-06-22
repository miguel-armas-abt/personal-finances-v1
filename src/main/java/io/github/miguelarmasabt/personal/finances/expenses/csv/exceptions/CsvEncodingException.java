package io.github.miguelarmasabt.personal.finances.expenses.csv.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

public class CsvEncodingException extends GenericException {

  public static final String ERROR_CODE = "0008";

  public CsvEncodingException(Throwable exception) {
    super(
        ERROR_CODE,
        "Could not encode CSV rows.",
        INTERNAL_SERVER_ERROR,
        exception
    );
  }
}
