package io.github.miguelarmasabt.commons.exceptions;

import io.github.miguelarmasabt.error.exceptions.GenericException;

import static io.github.miguelarmasabt.commons.constants.Errors.INVALID_OBJECT_ID;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

public class InvalidObjectIdFormatException extends GenericException {

  public InvalidObjectIdFormatException(String objectId) {
    super(INVALID_OBJECT_ID, BAD_REQUEST, objectId);
  }
}
