package com.example.foodking.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
@EnableCaching
/*
    setEnableTransactionSupport(true)를 통해 레디스의 트랜잭션과 jpa의 트랜잭션이 공유되게 하여
    jpa에 예외 발생 시 레디스의 데이터에 롤백이 발생하도록 하였다.
*/

public class RedisConfig {

    @Value("${spring.redis.auth.host}")
    private String authHost;

    @Value("${spring.redis.lock.host}")
    private String lockHost;

    @Value("${spring.redis.cache.host}")
    private String cacheHost;

    @Value("${spring.redis.lock.port}")
    private int lockPort;

    @Value("${spring.redis.auth.port}")
    private int authPort;

    @Value("${spring.redis.cache.port}")
    private int cachePort;

    private static final String REDISSON_HOST_PREFIX = "redis://";

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress(REDISSON_HOST_PREFIX + lockHost+":"+ lockPort);
        config.setCodec(new JsonJacksonCodec());
        return Redisson.create(config);
    }

    @Bean({"redisConnectionFactory", "cacheRedisConnectionFactory"})
    public RedisConnectionFactory cacheRedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(cacheHost);
        redisStandaloneConfiguration.setPort(cachePort);
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(
                redisStandaloneConfiguration);

        return lettuceConnectionFactory;
    }

    @Bean
    public RedisConnectionFactory authRedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(authHost);
        redisStandaloneConfiguration.setPort(authPort);
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(
                redisStandaloneConfiguration);

        return lettuceConnectionFactory;
    }

    @Bean
    public RedisTemplate<String, String> authRedis() {

        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(authRedisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        redisTemplate.setEnableTransactionSupport(true);

        return redisTemplate;
    }

    @Bean
    public RedisCacheManager redisCacheManager() {

        PolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType(Object.class)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.activateDefaultTyping(typeValidator,ObjectMapper.DefaultTyping.NON_FINAL);
        GenericJackson2JsonRedisSerializer redisSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
                .entryTtl(Duration.ofMinutes(30L));

        return RedisCacheManager.builder(cacheRedisConnectionFactory())
                .cacheDefaults(redisCacheConfiguration)
                .build();
    }
}
