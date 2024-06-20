package com.demo.product.configuration;


import com.demo.product.configuration.properties.GeneralProperties;
import com.demo.product.configuration.properties.RedisProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@EnableCaching
@Configuration
@RequiredArgsConstructor
public class RedisConfiguration {

    public static final String CACHE_METHOD = "Cache_Method";
    public static final String CACHE_METHOD_PREFIX = "Cache_Method_";

    private final RedisProperties redisProperties;
    private final GeneralProperties generalProperties;

    @Bean
    public RedisTemplate<?, ?> redisTemplate() {
        RedisTemplate<byte[], byte[]> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        return template;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConf = new RedisStandaloneConfiguration();
        redisConf.setHostName(redisProperties.getHost());
        redisConf.setPort(redisProperties.getPort());
        redisConf.setPassword(RedisPassword.of(redisProperties.getPassword()));
        return new LettuceConnectionFactory(redisConf);
    }

    @Bean
    public CacheManager cacheManager() {
        return RedisCacheManager.builder(redisConnectionFactory())
                .withInitialCacheConfigurations(constructInitialCacheConfigurations())
                .transactionAware()
                .build();
    }

    private Map<String, RedisCacheConfiguration> constructInitialCacheConfigurations() {
        final Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>();
        final RedisCacheConfiguration favoriteCountCache = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(generalProperties.getCountInSeconds()))
                .prefixCacheNameWith(CACHE_METHOD_PREFIX)
                .disableCachingNullValues();
        redisCacheConfigurationMap.put(CACHE_METHOD, favoriteCountCache);
        return redisCacheConfigurationMap;
    }
}