package com.example.foodking.config;

import com.example.foodking.common.DynamicRoutingDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Profile("prod")
public class DataSourceConfig {

    public static final String MASTER_DATASOURCE = "masterDataSource";
    public static final String SLAVE_DATASOURCE = "slaveDataSource";

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${spring.datasource.master.url}")
    private String masterUrl;

    @Value("${spring.datasource.username}")
    private String userName;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.slave.url}")
    private String slaveUrl;

    // Master 데이터베이스의 DataSource를 생성하는 빈 설정
    @Bean(MASTER_DATASOURCE)
    public DataSource masterDataSource() {
        return DataSourceBuilder.create()
                .driverClassName(driverClassName)
                .url(masterUrl)
                .username(userName)
                .password(password)
                .build();
    }

    // Slave 데이터베이스의 DataSource를 생성하는 빈 설정
    @Bean(SLAVE_DATASOURCE)
    public DataSource slaveDataSource() {
        return DataSourceBuilder.create()
                .driverClassName(driverClassName)
                .url(slaveUrl)
                .username(userName)
                .password(password)
                .build();
    }

    // 라우팅 데이터베이스를 생성하는 빈 설정
    @Bean
    public DataSource routingDataSource(@Qualifier(MASTER_DATASOURCE) DataSource masterDataSource,
                                        @Qualifier(SLAVE_DATASOURCE) DataSource slaveDataSource) {

        DynamicRoutingDataSource routingDataSource = new DynamicRoutingDataSource();

        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put("master", masterDataSource);
        dataSourceMap.put("slave", slaveDataSource);

        Map<Object, Object> immutableDataSourceMap = Collections.unmodifiableMap(dataSourceMap);

        routingDataSource.setTargetDataSources(immutableDataSourceMap);
        routingDataSource.setDefaultTargetDataSource(masterDataSource);

        return routingDataSource;
    }

    // 라우팅 데이터베이스를 기본 DataSource로 설정하는 빈 설정
    @Primary
    @Bean
    public DataSource dataSource(@Qualifier("routingDataSource") DataSource routingDataSource) {
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }

}
