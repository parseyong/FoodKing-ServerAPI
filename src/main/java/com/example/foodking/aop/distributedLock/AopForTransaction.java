package com.example.foodking.aop.distributedLock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AopForTransaction {

    // 락 해제 전에 트랜잭션 커밋이 되어야 하기 떄문에 트랜잭션 전파범위를 REQUIRES_NEW로 명시해주어야한다.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Object proceed(final ProceedingJoinPoint joinPoint) throws Throwable {

        return joinPoint.proceed();
    }
}