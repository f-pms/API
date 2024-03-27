package com.hbc.pms.support.web.error;

public class ForbiddenException extends CoreApiException {
  public ForbiddenException() {
    super(ErrorType.FORBIDDEN_ERROR, "You can't access this resource");
  }
}
