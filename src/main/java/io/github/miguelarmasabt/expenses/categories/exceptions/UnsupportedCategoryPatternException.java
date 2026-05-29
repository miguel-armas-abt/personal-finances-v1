package io.github.miguelarmasabt.expenses.categories.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

public class UnsupportedCategoryPatternException extends GenericException {

  public static final String ERROR_CODE = "0004";

  public UnsupportedCategoryPatternException(String code) {
    super(
        ERROR_CODE,
        "Unsupported category pattern: " + code,
        INTERNAL_SERVER_ERROR
    );
  }
}
