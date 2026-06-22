package io.github.miguelarmasabt.personal.finances.expenses.crud.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

public class InvalidDateException extends GenericException {

  public static final String ERROR_CODE = "0006";

  public InvalidDateException(String date) {
    super(
        ERROR_CODE,
        "The date must be in the format 'dd-MM-yyyy' or earlier than now: " + date,
        BAD_REQUEST
    );
  }
}
