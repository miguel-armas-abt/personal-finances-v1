package io.github.miguelarmasabt.expenses.crud.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

public class InvalidObjectIdFormatException extends GenericException {

  public static final String ERROR_CODE = "0007";

  public InvalidObjectIdFormatException(String objectId) {
    super(
        ERROR_CODE,
        "Invalid ObjectId format: " + objectId,
        BAD_REQUEST
    );
  }
}
