package com.demo.service.finances.expenses.extracted.exceptions;

import com.demo.commons.errors.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.CONFLICT;

public class NoSuchHeaderFromException extends GenericException {

  public static final String ERROR_CODE = "00.00.02";

  public NoSuchHeaderFromException() {
    super(
        ERROR_CODE,
        "No such header: 'From'",
        CONFLICT
    );
  }
}
