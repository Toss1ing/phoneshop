package com.es.core.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

@Aspect
public class ExceptionLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionLoggingAspect.class);

    @AfterThrowing(pointcut = "execution(* com.es.core.service..*(..))", throwing = "ex")
    public void logServiceExceptions(JoinPoint joinPoint, Throwable ex) {
        String message = ex.getMessage() != null ? ex.getMessage() : "";
        logger.error(
                "Exception in {}.{}() with arguments {}: {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()),
                message
        );
    }

}
