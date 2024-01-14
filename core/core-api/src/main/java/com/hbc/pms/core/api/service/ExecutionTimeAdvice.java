package com.hbc.pms.core.api.service;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Aspect
public class ExecutionTimeAdvice {

  @Around("@annotation(com.hbc.pms.core.model.TrackExecutionTime)")
  public Object executionTime(ProceedingJoinPoint point) throws Throwable {
    long startTime = System.currentTimeMillis();
    Object object = point.proceed();
    long endtime = System.currentTimeMillis();
    log.info("Class Name: "+ point.getSignature().getDeclaringTypeName() +". Method Name: "+ point.getSignature().getName() + ". Time taken for Execution is : " + (endtime-startTime) +"ms");
    return object;
  }
}
