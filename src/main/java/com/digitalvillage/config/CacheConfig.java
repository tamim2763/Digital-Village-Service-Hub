package com.digitalvillage.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.Objects;

/**
 * Cache configuration for weather data.
 * Uses simple in-memory ConcurrentMapCache with scheduled eviction.
 */
@Configuration
@EnableCaching
@EnableScheduling
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(List.of(
                new ConcurrentMapCache("weather")
        ));
        return cacheManager;
    }

    /**
     * Evict all weather cache entries every 30 minutes.
     */
    @Scheduled(fixedRate = 1800000) // 30 minutes
    public void evictWeatherCache() {
        Objects.requireNonNull(cacheManager().getCache("weather")).clear();
    }
}
