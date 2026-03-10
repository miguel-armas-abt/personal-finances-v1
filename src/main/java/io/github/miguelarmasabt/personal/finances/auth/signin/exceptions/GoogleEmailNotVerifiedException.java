package io.github.miguelarmasabt.personal.finances.auth.signin.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;
import jakarta.ws.rs.core.Response;

public class GoogleEmailNotVerifiedException extends GenericException {

  public static final String CODE = "5003";

  public GoogleEmailNotVerifiedException(String userCode) {
    super(CODE, Response.Status.UNAUTHORIZED, userCode);
  }
}
