package io.github.miguelarmasabt.personal.finances.expenses.bank.receipts.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

public class UnsupportedExtractExpenseStrategyException extends GenericException {

  public static final String ERROR_CODE = "0016";

  public UnsupportedExtractExpenseStrategyException(String from, String subject) {
    super(
        ERROR_CODE,
        "Unsupported extract expense strategy: " + from + ", " + subject,
        INTERNAL_SERVER_ERROR
    );
  }
}
