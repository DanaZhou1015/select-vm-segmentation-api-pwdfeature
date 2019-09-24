package com.acxiom.ams.aop;

import com.acxiom.ams.common.utils.LogUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * Created by cldong on 12/5/2017.
 */
@Aspect
@Component
public class ExceptionAspect {
    @AfterThrowing(value = "execution(* com.acxiom.ams.service..*.*(..))", throwing = "exception")
    public void doAfterThrowingAdvice(JoinPoint joinPoint, Throwable exception) {
        LogUtils.error("----------------throw service exception start----------------");
        LogUtils.error(joinPoint.getSignature().getName());
        LogUtils.error(exception);
        LogUtils.error(exception.getMessage());
        LogUtils.error("----------------throw service exception end----------------");
    }

    @Before("execution(* com.acxiom.ams.service..*.*(..))")
    public void doBeforeAdvice(JoinPoint joinPoint) {
        LogUtils.info("========================> start");
        Object[] obj = joinPoint.getArgs();
        joinPoint.getThis();
        joinPoint.getTarget();
        Signature signature = joinPoint.getSignature();
        LogUtils.info("------> method name : " + signature.getName() );
        for (Object o : obj) {
            LogUtils.info("------> value :" + (o == null ? null : o.toString()));
        }
        LogUtils.info("========================> end");
    }

}
