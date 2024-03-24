package com.hbc.pms.core.api.support.error;

public class ForbiddenException extends CoreApiException {
  public ForbiddenException() {
    super(ErrorType.FORBIDDEN_ERROR, "You can't access this resource");
  }
}
