package com.hbc.pms.core.api.support.error;

public class ReportExcelProcessorException extends RuntimeException {
  public ReportExcelProcessorException(String message) {
    super(message);
  }

  public ReportExcelProcessorException(String message, Throwable cause) {
    super(message, cause);
  }
}
