package io.github.miguelarmasabt.commons.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

public class UserCodeRequiredException extends GenericException {

  public static final String CODE = "1004";

  public UserCodeRequiredException() {
    super(CODE, BAD_REQUEST);
  }
}
