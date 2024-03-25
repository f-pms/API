package com.hbc.pms.plc.api.exceptions;

import org.apache.plc4x.java.api.exceptions.PlcException;

public class WritePlcException extends PlcException {

  public WritePlcException(String message) {
    super(message);
  }

  public WritePlcException(String message, Throwable cause) {
    super(message, cause);
  }

  public WritePlcException(Throwable cause) {
    super(cause);
  }

  public WritePlcException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
