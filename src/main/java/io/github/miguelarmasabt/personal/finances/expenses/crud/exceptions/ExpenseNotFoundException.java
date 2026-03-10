package io.github.miguelarmasabt.personal.finances.expenses.crud.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;

import static io.github.miguelarmasabt.commons.constants.Errors.EXPENSE_NOT_FOUND;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;

public class ExpenseNotFoundException extends GenericException {

  public ExpenseNotFoundException(String id) {
    super(EXPENSE_NOT_FOUND, NOT_FOUND, id);
  }
}
