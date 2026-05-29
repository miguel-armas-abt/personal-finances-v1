package io.github.miguelarmasabt.commons.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

public class UserCodeRequiredException extends GenericException {

  public static final String ERROR_CODE = "0003";

  public UserCodeRequiredException() {
    super(
        ERROR_CODE,
        "The field user email is required",
        BAD_REQUEST
    );
  }
}
