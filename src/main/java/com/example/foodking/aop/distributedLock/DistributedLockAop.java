package com.example.foodking.aop.distributedLock;

import com.example.foodking.exception.CommondException;
import com.example.foodking.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
@Log4j2
public class DistributedLockAop {
    private static final String REDISSON_LOCK_PREFIX = "LOCK:";

    private final RedissonClient redissonClient;
    private final AopForTransaction aopForTransaction;

    @Around("@annotation(com.example.foodking.aop.distributedLock.DistributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        
        // joinPoint의 메소드 서명 가져오기
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        // jointPoint의 메소드 객체 가져오기
        Method method = signature.getMethod();
        
        // jointPoint의 메소드 파라미터 값 가져오기
        Object[] args = joinPoint.getArgs();

        // 메소드에 선언된 @DistributedLock 어노테이션 정보 가져오기
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        // SpringELParser를 통해 파라미터값에 따라 동적으로 키를 생성해 키의 중복을 방지한다.
        String key = REDISSON_LOCK_PREFIX + distributedLock.key() + method.getName() + args[1];

        // lock 획득시도
        RLock rLock = redissonClient.getLock(key);

        try {
            boolean available = rLock.tryLock
                    (distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());

            // lock획득에 실패하면 pub/sub기반으로 lock재획득을 시도한다. waitTime이 초과되면 InterruptedException 발생
            if (!available) {
                log.info("락 획득 실패");
                throw new CommondException(ExceptionCode.LOCK_CAPTURE_FAIL);
            }

            // lock을 얻었다면 트랜잭션을 시작한다.
            log.info(key);
            return aopForTransaction.proceed(joinPoint);

        } catch (InterruptedException e) {
            // waitTime이나 leaseTime등의 이유로 interrupted 발생 시
            log.info("Interrupted lock");
            throw new CommondException(ExceptionCode.LOCK_CAPTURE_FAIL);

        } finally {

            // 트랜잭션 커밋 이후 락이 해제되도록 finally절에 락 해제 선언
            if (rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }
}
