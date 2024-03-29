package com.hbc.pms.support.web.error;

import lombok.Getter;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorType {
  NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, ErrorCode.E404, "Resource not found", LogLevel.WARN),
  BAD_REQUEST_ERROR(HttpStatus.BAD_REQUEST, ErrorCode.E400, "Bad request", LogLevel.WARN),
  FORBIDDEN_ERROR(HttpStatus.FORBIDDEN, ErrorCode.E403, "Forbidden", LogLevel.WARN),
  DEFAULT_ERROR(
      HttpStatus.INTERNAL_SERVER_ERROR,
      ErrorCode.E500,
      "An unexpected error has occurred.",
      LogLevel.ERROR);

  private final HttpStatus status;

  private final ErrorCode code;

  private final String message;

  private final LogLevel logLevel;

  ErrorType(HttpStatus status, ErrorCode code, String message, LogLevel logLevel) {
    this.status = status;
    this.code = code;
    this.message = message;
    this.logLevel = logLevel;
  }
}
