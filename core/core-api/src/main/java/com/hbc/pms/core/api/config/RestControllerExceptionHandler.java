package com.hbc.pms.core.api.config;

import com.hbc.pms.core.api.support.error.CoreApiException;
import com.hbc.pms.core.api.support.error.ErrorType;
import com.hbc.pms.core.api.support.response.ApiResponse;
import org.modelmapper.ValidationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class RestControllerExceptionHandler {
    @ResponseBody
    @ExceptionHandler({CoreApiException.class})
    public ApiResponse<RuntimeException> handleException(CoreApiException ex) {
        return ApiResponse.error(ex.getErrorType(), ex.getData());
    }

  @ResponseBody
  @ExceptionHandler({ValidationException.class})
  public ApiResponse<RuntimeException> handleMapperException(ValidationException ex) {
    return ApiResponse.error(ErrorType.BAD_REQUEST_ERROR, ex.getErrorMessages());
  }
}
