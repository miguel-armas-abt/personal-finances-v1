package com.demo.service.finances.expenses.crud.exceptions;

import com.demo.commons.errors.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

public class InvalidObjectIdFormatException extends GenericException {

  public static final String ERROR_CODE = "00.00.03";

  public InvalidObjectIdFormatException(String objectId) {
    super(
        ERROR_CODE,
        "Invalid ObjectId format: " + objectId,
        BAD_REQUEST
    );
  }
}
