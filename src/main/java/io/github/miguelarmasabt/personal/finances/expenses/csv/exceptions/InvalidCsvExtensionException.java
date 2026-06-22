package io.github.miguelarmasabt.personal.finances.expenses.csv.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

public class InvalidCsvExtensionException extends GenericException {

  public static final String ERROR_CODE = "0010";

  public InvalidCsvExtensionException() {
    super(
        ERROR_CODE,
        "File extension must be .csv.",
        BAD_REQUEST
    );
  }
}
