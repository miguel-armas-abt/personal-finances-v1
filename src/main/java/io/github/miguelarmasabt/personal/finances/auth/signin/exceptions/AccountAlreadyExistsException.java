package io.github.miguelarmasabt.personal.finances.auth.signin.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;
import jakarta.ws.rs.core.Response;

public class AccountAlreadyExistsException extends GenericException {

  public static final String CODE = "5000";

  public AccountAlreadyExistsException(String userCode) {
    super(CODE, Response.Status.CONFLICT, userCode);
  }
}
