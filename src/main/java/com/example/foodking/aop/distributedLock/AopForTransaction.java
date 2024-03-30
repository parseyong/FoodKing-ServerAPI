package com.example.foodking.aop.distributedLock;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Log4j2
public class AopForTransaction {

    // 락 해제 전에 트랜잭션 커밋이 되어야 하기 떄문에 트랜잭션 전파범위를 REQUIRES_NEW로 명시해주어야한다.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Object proceed(final ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("락 획득 성공");
        return joinPoint.proceed();
    }
}