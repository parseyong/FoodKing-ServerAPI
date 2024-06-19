package com.example.foodking.aop.distributedLock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AopForTransaction {

    // 부모 트랜잭션의 유무와 관계없이 별도의 트랜잭션을 생성하기 위해 전파범위를 REQUIRES_NEW로 설정
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Object proceed(final ProceedingJoinPoint joinPoint) throws Throwable {

        return joinPoint.proceed();
    }
}