package com.example.foodking.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedissonConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.lock.port}")
    private int lockPort;

    @Value("${spring.redis.auth.port}")
    private int authPort;

    @Value("${spring.redis.cache.port}")
    private int cachePort;

    private static final String REDISSON_HOST_PREFIX = "redis://";

    @Bean
    @Primary
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress(REDISSON_HOST_PREFIX + host+":"+ lockPort);
        config.setCodec(new JsonJacksonCodec());
        return Redisson.create(config);
    }

    @Bean
    @Qualifier("authNumberRedis")
    public RedissonClient authNumberRedis() {
        Config config = new Config();
        config.useSingleServer().setAddress(REDISSON_HOST_PREFIX + host+":"+ authPort);
        config.setCodec(new JsonJacksonCodec());
        return Redisson.create(config);
    }

    @Bean
    @Qualifier("isAuthNumberRedis")
    public RedissonClient isAuthNumberRedis() {
        Config config = new Config();
        config.useSingleServer().setAddress(REDISSON_HOST_PREFIX + host+":"+ authPort);
        config.setCodec(new JsonJacksonCodec());
        return Redisson.create(config);
    }

    @Bean
    @Qualifier("tokenRedis")
    public RedissonClient tokenRedis() {
        Config config = new Config();
        config.useSingleServer().setAddress(REDISSON_HOST_PREFIX + host+":"+ authPort);
        config.setCodec(new JsonJacksonCodec());
        return Redisson.create(config);
    }

    @Bean
    @Qualifier("blackListRedis")
    public RedissonClient blackListRedis() {
        Config config = new Config();
        config.useSingleServer().setAddress(REDISSON_HOST_PREFIX + host+":"+authPort);
        config.setCodec(new JsonJacksonCodec());
        return Redisson.create(config);
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, cachePort);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());

        return redisTemplate;
    }
}
