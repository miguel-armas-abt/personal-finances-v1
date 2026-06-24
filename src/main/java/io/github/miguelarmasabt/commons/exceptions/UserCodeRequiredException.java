package io.github.miguelarmasabt.commons.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;

import static io.github.miguelarmasabt.commons.constants.Errors.REQUIRED_USER_CODE;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

public class UserCodeRequiredException extends GenericException {

  public UserCodeRequiredException() {
    super(REQUIRED_USER_CODE, BAD_REQUEST);
  }
}
