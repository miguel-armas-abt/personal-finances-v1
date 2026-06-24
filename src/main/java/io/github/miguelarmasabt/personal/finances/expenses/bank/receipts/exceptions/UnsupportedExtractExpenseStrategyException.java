package io.github.miguelarmasabt.personal.finances.expenses.bank.receipts.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;

import static io.github.miguelarmasabt.commons.constants.Errors.UNSUPPORTED_EXTRACT_EXPENSE_STRATEGY;
import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

public class UnsupportedExtractExpenseStrategyException extends GenericException {

  public UnsupportedExtractExpenseStrategyException(String from, String subject) {
    super(UNSUPPORTED_EXTRACT_EXPENSE_STRATEGY, INTERNAL_SERVER_ERROR, from, subject);
  }
}
