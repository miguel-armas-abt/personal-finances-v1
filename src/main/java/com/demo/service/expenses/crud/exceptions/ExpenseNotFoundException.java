package com.demo.service.expenses.crud.exceptions;

import com.demo.commons.error.exceptions.GenericException;

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
