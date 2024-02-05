package com.hbc.pms.plc.api.exceptions;

import org.apache.plc4x.java.api.exceptions.PlcException;

public class NotSupportedPlcResponseException extends PlcException {

  public NotSupportedPlcResponseException(String message) {
    super(message);
  }

  public NotSupportedPlcResponseException(String message, Throwable cause) {
    super(message, cause);
  }

  public NotSupportedPlcResponseException(Throwable cause) {
    super(cause);
  }

  public NotSupportedPlcResponseException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
