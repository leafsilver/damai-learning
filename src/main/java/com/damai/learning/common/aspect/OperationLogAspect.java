package com.damai.learning.common.aspect;

import com.damai.learning.common.annotation.OperationLog;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 关键业务操作日志切面。
 */
@Aspect
@Component
public class OperationLogAspect {

    private static final Logger log =
            LoggerFactory.getLogger(OperationLogAspect.class);

    /**
     * 匹配所有标记了OperationLog的方法。
     */
    @Pointcut(
            "@annotation(com.damai.learning.common.annotation.OperationLog)"
    )
    public void operationLogPointcut() {
    }

    /**
     * 记录业务方法的执行结果和耗时。
     */
    @Around(
            "operationLogPointcut() && @annotation(operationLog)"
    )
    public Object recordOperation(
            ProceedingJoinPoint joinPoint,
            OperationLog operationLog
    ) throws Throwable {
        String className = joinPoint
                .getSignature()
                .getDeclaringType()
                .getSimpleName();

        String methodName = joinPoint
                .getSignature()
                .getName();

        long startTime = System.nanoTime();

        try {
            Object result = joinPoint.proceed();

            long elapsedMillis = calculateElapsedMillis(
                    startTime
            );

            log.info(
                    "业务操作成功，operation={}，method={}.{}，elapsedMs={}",
                    operationLog.value(),
                    className,
                    methodName,
                    elapsedMillis
            );

            return result;
        } catch (Throwable throwable) {
            long elapsedMillis = calculateElapsedMillis(
                    startTime
            );

            log.warn(
                    "业务操作失败，operation={}，method={}.{}，elapsedMs={}，exception={}",
                    operationLog.value(),
                    className,
                    methodName,
                    elapsedMillis,
                    throwable.getClass().getSimpleName()
            );

            throw throwable;
        }
    }

    private long calculateElapsedMillis(long startTime) {
        long elapsedNanos =
                System.nanoTime() - startTime;

        return TimeUnit.NANOSECONDS.toMillis(
                elapsedNanos
        );
    }
}