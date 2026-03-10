package io.github.miguelarmasabt.personal.finances.expenses.crud.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;

public class ExpenseNotFoundException extends GenericException {

  public static final String CODE = "4001";

  public ExpenseNotFoundException(String id) {
    super(CODE, NOT_FOUND, id);
  }
}
