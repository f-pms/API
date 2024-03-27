package com.hbc.pms.core.api.config;

import com.hbc.pms.support.web.error.CoreApiException;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

@Slf4j
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

  @Override
  public void handleUncaughtException(Throwable e, Method method, Object... params) {
    if (e instanceof CoreApiException) {
      switch (((CoreApiException) e).getErrorType().getLogLevel()) {
        case ERROR -> log.error("CoreApiException : {}", e.getMessage(), e);
        case WARN -> log.warn("CoreApiException : {}", e.getMessage(), e);
        default -> log.info("CoreApiException : {}", e.getMessage(), e);
      }
    } else {
      log.error("Exception : {}", e.getMessage(), e);
    }
  }
}
