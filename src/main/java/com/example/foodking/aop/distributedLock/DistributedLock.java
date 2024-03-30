package com.example.foodking.aop.distributedLock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/*
    @Target : 적용대상, 메소드에 해당 어노테이션이 적용될 수 있도록 지정
    @Retention : 컴파일러가 어노테이션을 다루는 방법을 기술, 어느 시점까지 영향을 미치는지를 결정
    - RetentionPolicy.SOURCE : 컴파일 전까지만 유효
    - RetentionPolicy.CLASS : 컴파일러가 클래스를 참조할 때까지 유효
    - RetentionPolicy.RUNTIME : 컴파일 이후 런타임 시기에도 JVM에 의해 참조가 가능(리플렉션)
*/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

    // 락 이름
    String key();

    // 락 시간 단위
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    // 락 획득을 위해 기다리는 시간
    long waitTime() default 5L;

    // 락 임대 시간
    long leaseTime() default 3L;
}