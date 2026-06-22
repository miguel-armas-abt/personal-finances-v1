package io.github.miguelarmasabt.personal.finances.expenses.bank.receipts.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.CONFLICT;

public class NoSuchHeaderFromException extends GenericException {

  public static final String ERROR_CODE = "0015";

  public NoSuchHeaderFromException() {
    super(
        ERROR_CODE,
        "No such header: 'From'",
        CONFLICT
    );
  }
}
