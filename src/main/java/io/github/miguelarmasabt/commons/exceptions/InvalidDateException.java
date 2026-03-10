package io.github.miguelarmasabt.commons.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

public class InvalidDateException extends GenericException {

  public static final String CODE = "1001";

  public InvalidDateException(String date) {
    super(CODE, BAD_REQUEST, date);
  }
}
