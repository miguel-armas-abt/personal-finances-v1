package io.github.miguelarmasabt.personal.finances.expenses.categories.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;

import static io.github.miguelarmasabt.commons.constants.Errors.UNSUPPORTED_CATEGORY_PATTERN;
import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

public class UnsupportedCategoryPatternException extends GenericException {

  public UnsupportedCategoryPatternException(String code) {
    super(UNSUPPORTED_CATEGORY_PATTERN, INTERNAL_SERVER_ERROR, code);
  }
}
