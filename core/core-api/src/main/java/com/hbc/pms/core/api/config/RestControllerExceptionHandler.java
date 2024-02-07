package com.hbc.pms.core.api.config;

import com.hbc.pms.core.api.support.error.CoreApiException;
import com.hbc.pms.core.api.support.error.ErrorType;
import com.hbc.pms.core.api.support.response.ApiResponse;
import java.util.HashMap;
import java.util.Map;
import org.modelmapper.ValidationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

  @ResponseBody
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ApiResponse<RuntimeException> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            error -> {
              String fieldName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();
              errors.put(fieldName, errorMessage);
            });
    return ApiResponse.error(ErrorType.BAD_REQUEST_ERROR, errors);
  }

  @ResponseBody
  @ExceptionHandler(RuntimeException.class)
  public ApiResponse<RuntimeException> handleValidationExceptions(RuntimeException ex) {
    return ApiResponse.error(ErrorType.BAD_REQUEST_ERROR, ex.toString());
  }
}
