package com.ticketing.review.common.aspect;

import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
@Profile({"dev", "local"})
public class LogAspect {


  @Pointcut("execution(* com.ticketing.review..*Controller.*(..))")
  public void controller() {
  }

  @Pointcut("execution(* com.ticketing.review..*Service.*(..))")
  public void service() {
  }

  @Around("controller() || service()")
  public Object logging(ProceedingJoinPoint joinPoint) throws Throwable {
    long startTime = System.currentTimeMillis();
    try {
      MethodSignature signature = (MethodSignature) joinPoint.getSignature();
      Method method = signature.getMethod();
      log.info("========== method name = {} ==========", method.getName());

      Object[] args = joinPoint.getArgs();
      if (args.length == 0) {
        log.info("args is empty");
      }
      for (Object arg : args) {
        log.info("parameter type = {}", args.getClass().getSimpleName());
        log.info("parameter value = {}", arg);
      }

      Object result = joinPoint.proceed();
      if (result != null) {
        log.info("return type = {}", result.getClass().getSimpleName());
        log.info("return value = {}", result);
      }
      return result;
    } finally {
      long endTime = System.currentTimeMillis();
      long timeMs = endTime - startTime;
      log.info("timeMs = {}", timeMs);

    }
  }


}
