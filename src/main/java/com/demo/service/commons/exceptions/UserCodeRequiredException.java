package com.demo.service.commons.exceptions;

import com.demo.commons.errors.exceptions.GenericException;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

public class UserCodeRequiredException extends GenericException {

  public static final String ERROR_CODE = "00.00.03";

  public UserCodeRequiredException() {
    super(
        ERROR_CODE,
        "The field user email is required",
        BAD_REQUEST
    );
  }
}
