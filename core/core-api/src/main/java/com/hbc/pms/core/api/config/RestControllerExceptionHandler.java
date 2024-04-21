package com.hbc.pms.core.api.config;

import com.hbc.pms.support.web.error.CoreApiException;
import com.hbc.pms.support.web.error.ErrorType;
import com.hbc.pms.support.web.response.ApiResponse;
import io.jsonwebtoken.JwtException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.MappingException;
import org.modelmapper.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
@Slf4j
public class RestControllerExceptionHandler {

  @ResponseBody
  @ExceptionHandler({CoreApiException.class})
  public ResponseEntity<ApiResponse<RuntimeException>> handleException(CoreApiException ex) {
    return new ResponseEntity<>(
        ApiResponse.error(ex.getErrorType(), ex.getData()), ex.getErrorType().getStatus());
  }

  @ResponseBody
  @ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
  @ExceptionHandler({ValidationException.class, MappingException.class})
  public ApiResponse<?> handleMapperException(Exception ex) {
    return ApiResponse.error(ErrorType.BAD_REQUEST_ERROR, ex.getMessage());
  }

  @ResponseBody
  @ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
  @ExceptionHandler({MethodArgumentTypeMismatchException.class})
  public ApiResponse<RuntimeException> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException ex) {
    return ApiResponse.error(ErrorType.BAD_REQUEST_ERROR, ex.getMessage());
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
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ApiResponse<RuntimeException>> handleValidationExceptions(
      RuntimeException ex) {
    if (ex instanceof BadCredentialsException) {
      return new ResponseEntity<>(
          ApiResponse.error(ErrorType.BAD_REQUEST_ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
    log.error(ex.getMessage(), ex); // keep stacktrace
    return new ResponseEntity<>(
        ApiResponse.error(ErrorType.DEFAULT_ERROR, ex.toString()),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ResponseBody
  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ExceptionHandler(AccessDeniedException.class)
  public void handleAccessDenied() {}

  @ResponseBody
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler(JwtException.class)
  public void handleUnauthorized() {}
}
