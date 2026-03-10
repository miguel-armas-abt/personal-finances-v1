package io.github.miguelarmasabt.commons.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;

import static io.github.miguelarmasabt.commons.constants.Errors.INVALID_DATE;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

public class InvalidDateException extends GenericException {

  public InvalidDateException(String date) {
    super(INVALID_DATE, BAD_REQUEST, date);
  }
}
