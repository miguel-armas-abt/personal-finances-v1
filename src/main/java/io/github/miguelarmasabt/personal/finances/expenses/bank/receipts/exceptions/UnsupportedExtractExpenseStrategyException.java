package io.github.miguelarmasabt.personal.finances.expenses.bank.receipts.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

public class UnsupportedExtractExpenseStrategyException extends GenericException {

  public static final String CODE = "3001";

  public UnsupportedExtractExpenseStrategyException(String from, String subject) {
    super(CODE, INTERNAL_SERVER_ERROR, from, subject);
  }
}
