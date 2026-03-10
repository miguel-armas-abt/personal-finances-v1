package com.demo.service.finances.expenses.crud.exceptions;

import com.demo.commons.errors.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;

public class ExpenseNotFoundException extends GenericException {

  public static final String ERROR_CODE = "00.00.02";

  public ExpenseNotFoundException(String id) {
    super(
        ERROR_CODE,
        "Expense not found: " + id,
        NOT_FOUND
    );
  }
}
