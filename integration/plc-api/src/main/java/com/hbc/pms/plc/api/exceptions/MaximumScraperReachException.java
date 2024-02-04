package com.hbc.pms.plc.api.exceptions;

import org.apache.plc4x.java.api.exceptions.PlcException;

public class MaximumScraperReachException extends PlcException {
  public MaximumScraperReachException(String message) {
    super(message);
  }

  public MaximumScraperReachException(String message, Throwable cause) {
    super(message, cause);
  }

  public MaximumScraperReachException(Throwable cause) {
    super(cause);
  }

  public MaximumScraperReachException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
