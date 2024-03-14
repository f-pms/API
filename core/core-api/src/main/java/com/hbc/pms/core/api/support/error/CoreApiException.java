package com.hbc.pms.core.api.support.error;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
public class CoreApiException extends RuntimeException {

  private final ErrorType errorType;

  private final Object data;

  public CoreApiException(ErrorType errorType) {
    super(errorType.getMessage());
    this.errorType = errorType;
    this.data = null;
  }

  public CoreApiException(ErrorType errorType, Object data) {
    super(errorType.getMessage());
    this.errorType = errorType;
    this.data = data;
  }

  public ErrorType getErrorType() {
    return errorType;
  }

  public Object getData() {
    return data;
  }
}
