package io.github.miguelarmasabt.personal.finances.auth.signin.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;
import jakarta.ws.rs.core.Response;

public class DisabledUserException extends GenericException {

  public static final String CODE = "5001";

  public DisabledUserException(String userCode) {
    super(CODE, Response.Status.FORBIDDEN, userCode);
  }
}
