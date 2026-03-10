package io.github.miguelarmasabt.personal.finances.expenses.categories.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

public class UnsupportedCategoryPatternException extends GenericException {

  public static final String CODE = "4000";

  public UnsupportedCategoryPatternException(String code) {
    super(CODE, INTERNAL_SERVER_ERROR, code);
  }
}
