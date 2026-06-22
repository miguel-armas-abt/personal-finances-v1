package io.github.miguelarmasabt.personal.finances.expenses.crud.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;

public class ExpenseNotFoundException extends GenericException {

  public static final String ERROR_CODE = "0005";

  public ExpenseNotFoundException(String id) {
    super(
        ERROR_CODE,
        "Expense not found: " + id,
        NOT_FOUND
    );
  }
}
