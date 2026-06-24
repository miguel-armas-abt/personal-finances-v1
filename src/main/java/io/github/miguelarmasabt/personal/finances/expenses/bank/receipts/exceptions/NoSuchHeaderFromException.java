package io.github.miguelarmasabt.personal.finances.expenses.bank.receipts.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;

import static io.github.miguelarmasabt.commons.constants.Errors.NO_SUCH_HEADER_FROM;
import static jakarta.ws.rs.core.Response.Status.CONFLICT;

public class NoSuchHeaderFromException extends GenericException {

  public NoSuchHeaderFromException() {
    super(NO_SUCH_HEADER_FROM, CONFLICT);
  }
}
