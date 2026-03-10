package io.github.miguelarmasabt.personal.finances.auth.refresh.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;
import jakarta.ws.rs.core.Response;

public class InvalidRefreshTokenException extends GenericException {

  public static final String CODE = "5002";

  public InvalidRefreshTokenException() {
    super(CODE, Response.Status.UNAUTHORIZED);
  }
}
