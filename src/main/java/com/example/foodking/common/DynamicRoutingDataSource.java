package com.example.foodking.common;

import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Log4j2
public class DynamicRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    // 현재 데이터베이스 연결을 결정하기 위해 호출하는 메서드
    protected Object determineCurrentLookupKey() {
        // 현재 트랜잭션이 읽기 전용인지 여부를 판단합니다.
        boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();

        if(isReadOnly)
            log.info("slave db사용");
        else
            log.info("master db사용");

        // 현재 트랜잭션이 읽기 전용인 경우는 "slave", 아닐 경우 "master"를 반환하여 데이터베이스 연결을 결정
        return isReadOnly ? "slave" : "master";
    }
}