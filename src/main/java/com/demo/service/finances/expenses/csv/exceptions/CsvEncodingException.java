package com.demo.service.finances.expenses.csv.exceptions;

import com.demo.commons.errors.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

public class CsvEncodingException extends GenericException {

  public static final String ERROR_CODE = "00.00.01";

  public CsvEncodingException(Throwable exception) {
    super(
        ERROR_CODE,
        exception,
        "Could not encode CSV rows.",
        INTERNAL_SERVER_ERROR
    );
  }
}
