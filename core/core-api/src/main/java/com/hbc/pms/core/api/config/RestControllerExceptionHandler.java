package com.hbc.pms.core.api.config;

import com.hbc.pms.core.api.support.error.CoreApiException;
import com.hbc.pms.core.api.support.error.ErrorType;
import com.hbc.pms.core.api.support.response.ApiResponse;
import java.util.HashMap;
import java.util.Map;
import org.modelmapper.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class RestControllerExceptionHandler {

  @ResponseBody
  @ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
  @ExceptionHandler({CoreApiException.class})
  public ApiResponse<RuntimeException> handleException(CoreApiException ex) {
    return ApiResponse.error(ex.getErrorType(), ex.getData());
  }

  @ResponseBody
  @ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
  @ExceptionHandler({ValidationException.class})
  public ApiResponse<RuntimeException> handleMapperException(ValidationException ex) {
    return ApiResponse.error(ErrorType.BAD_REQUEST_ERROR, ex.getErrorMessages());
  }

  @ResponseBody
  @ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
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
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(RuntimeException.class)
  public ApiResponse<RuntimeException> handleValidationExceptions(RuntimeException ex) {
    return ApiResponse.error(ErrorType.DEFAULT_ERROR, ex.toString());
  }
}
