package com.demo.service.finances.expenses.categories.exceptions;

import com.demo.commons.errors.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

public class UnsupportedCategoryPatternException extends GenericException {

  public static final String ERROR_CODE = "00.00.02";

  public UnsupportedCategoryPatternException(String code) {
    super(
        ERROR_CODE,
        "Unsupported category pattern: " + code,
        INTERNAL_SERVER_ERROR
    );
  }
}
