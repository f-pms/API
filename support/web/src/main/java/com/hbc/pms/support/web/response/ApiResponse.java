package com.hbc.pms.support.web.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hbc.pms.support.web.error.ErrorMessage;
import com.hbc.pms.support.web.error.ErrorType;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<S> {

  private final ResultType result;

  private final S data;

  private final ErrorMessage error;

  private ApiResponse(ResultType result, S data, ErrorMessage error) {
    this.result = result;
    this.data = data;
    this.error = error;
  }

  public static ApiResponse<?> success() {
    return new ApiResponse<>(ResultType.SUCCESS, null, null);
  }

  public static <S> ApiResponse<S> success(S data) {
    return new ApiResponse<>(ResultType.SUCCESS, data, null);
  }

  public static ApiResponse<?> error(ErrorType error) {
    return new ApiResponse<>(ResultType.ERROR, null, new ErrorMessage(error));
  }

  public static ApiResponse<RuntimeException> error(ErrorType error, Object errorData) {
    return new ApiResponse<>(ResultType.ERROR, null, new ErrorMessage(error, errorData));
  }
}
