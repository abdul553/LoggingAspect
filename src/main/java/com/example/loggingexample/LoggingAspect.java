package com.example.loggingexample;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    @Autowired
    private ObjectMapper mapper;

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void pointcut() {
    }

    @Pointcut("within(com.example.loggingexample..*)" +
            " || within(com.example.loggingexample.service..*)" +
            " || within(com.example.loggingexample.controller..*)")
    public void applicationPackagePointcut() {

    }

    @Before("pointcut()")
    public void logMethodBefore(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RequestMapping mapping = signature.getMethod().getAnnotation(RequestMapping.class);
        Map<String, Object> parameters = getParameters(joinPoint);

        try {
            logger.info("Logging Before: { timestamp: {}, path: {}, method: {}, arguments: {} }",
                    new Timestamp(System.currentTimeMillis()), mapping.path(), mapping.method(), mapper.writeValueAsString(parameters.toString()));
        } catch (JsonProcessingException e) {
            logger.error("Error while converting", e);
        }
    }

    @AfterReturning(pointcut = "pointcut()", returning = "entity")
    public void logMethodAfter(JoinPoint joinPoint, ResponseEntity<?> entity) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RequestMapping mapping = signature.getMethod().getAnnotation(RequestMapping.class);
        try {
            logger.info("Logging After: { timestamp: {}, path: {}, method: {}, retuning: {} }",
                    new Timestamp(System.currentTimeMillis()), mapping.path(), mapping.method(), mapper.writeValueAsString(entity));
        } catch (JsonProcessingException e) {
            logger.error("Error while converting", e);
        }
    }

    @AfterThrowing(pointcut = "pointcut() || applicationPackagePointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        logger.error("Logging { Exception in {}.{}() with cause = {} }", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), e.getCause() != null ? e.getCause() : "NULL");
    }

    @Pointcut("@annotation(com.example.loggingexample.AutoLogging)")
    public void customLogging() {}

    @Around("customLogging()")
    public String customLogging(ProceedingJoinPoint joinPoint) {
        Map<String, Object> params = new HashMap<>();
        params.put("Controller", joinPoint.getSignature().getDeclaringType().getSimpleName());
        params.put("Method", "/"+joinPoint.getSignature().getName());
        params.put("Time", new Date());
        logger.info("Logging {}", params);
        return "";
    }
    // class service class
    @Around("execution(* com.example.loggingexample.service.LogService.*(..))")
    public Object loggingService(ProceedingJoinPoint pjp) throws Throwable{
        Object retVal = pjp.proceed();
        logger.info("Logging service " + retVal);
        return retVal;
    }



    /*
    @Around("loggingOnlyException()")
    public void methodLogging(ProceedingJoinPoint joinPoint) {
        Map<String, Object> params = new HashMap<>();
        params.put("Controller", joinPoint.getSignature().getDeclaringType().getSimpleName());
        params.put("Method", "/"+joinPoint.getSignature().getName());
        params.put("Time", new Date());
        logger.info("Logging {}", params);
    }*/
    private Map<String, Object> getParameters(JoinPoint joinPoint) {
        CodeSignature signature = (CodeSignature) joinPoint.getSignature();

        HashMap<String, Object> map = new HashMap<>();
        String[] parameterNames = signature.getParameterNames();
        for (int i = 0; i < parameterNames.length; i++) {
            map.put(parameterNames[i], joinPoint.getArgs()[i]);
        }
        return map;
    }



}