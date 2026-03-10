package io.github.miguelarmasabt.personal.finances.auth.signin.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;
import jakarta.ws.rs.core.Response;

public class InvalidGoogleIdTokenException extends GenericException {

  public static final String CODE = "3003";

  public InvalidGoogleIdTokenException() {
    super(CODE, Response.Status.UNAUTHORIZED);
  }
}
