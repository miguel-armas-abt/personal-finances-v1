package io.github.miguelarmasabt.personal.finances.expenses.bank.receipts.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.CONFLICT;

public class NoSuchHeaderFromException extends GenericException {

  public static final String CODE = "3000";

  public NoSuchHeaderFromException() {
    super(CODE, CONFLICT);
  }
}
