package io.github.miguelarmasabt.commons.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

public class InvalidObjectIdFormatException extends GenericException {

  public static final String CODE = "1000";

  public InvalidObjectIdFormatException(String objectId) {
    super(CODE, BAD_REQUEST, objectId);
  }
}
