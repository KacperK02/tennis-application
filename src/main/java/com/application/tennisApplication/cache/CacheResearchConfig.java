package com.application.tennisApplication.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Arrays;

@Configuration
@EnableCaching
public class CacheResearchConfig {
    // Nowa flaga w application.properties (caffeine, lru, fifo, random)
    @Value("${app.cache.algorithm:caffeine}")
    private String cacheAlgorithm;

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        System.out.println("Inicjalizacja środowiska badawczego. Algorytm: " + cacheAlgorithm.toUpperCase());

        // LIMIT DO BADAŃ: Ustawiamy maksymalny rozmiar na bardzo mały.
        // Dzięki temu szybko wymusimy proces wypierania podczas testów
        int CACHE_MAX_SIZE = 10;

        if ("caffeine".equalsIgnoreCase(cacheAlgorithm)) {
            // W-TinyLFU (Caffeine)
            CaffeineCacheManager caffeineManager = new CaffeineCacheManager();
            caffeineManager.setCaffeine(Caffeine.newBuilder()
                    .maximumSize(CACHE_MAX_SIZE)
                    .recordStats()); // pozwala potem wyciągnąć hit rate
            return caffeineManager;

        } else if ("lru".equalsIgnoreCase(cacheAlgorithm)) {
            SimpleCacheManager lruManager = new SimpleCacheManager();
            lruManager.setCaches(Arrays.asList(
                    new ResearchCache("wtaPlayersCache", CacheAlgorithms.createLRUMap(CACHE_MAX_SIZE)),
                    new ResearchCache("atpPlayersCache", CacheAlgorithms.createLRUMap(CACHE_MAX_SIZE)),
                    new ResearchCache("playerPhotosCache", CacheAlgorithms.createLRUMap(CACHE_MAX_SIZE)),
                    new ResearchCache("matchStatsCache", CacheAlgorithms.createLRUMap(CACHE_MAX_SIZE))
            ));
            return lruManager;

        } else if ("fifo".equalsIgnoreCase(cacheAlgorithm)) {
            SimpleCacheManager fifoManager = new SimpleCacheManager();
            fifoManager.setCaches(Arrays.asList(
                    new ResearchCache("wtaPlayersCache", CacheAlgorithms.createFIFOMap(CACHE_MAX_SIZE)),
                    new ResearchCache("atpPlayersCache", CacheAlgorithms.createFIFOMap(CACHE_MAX_SIZE)),
                    new ResearchCache("playerPhotosCache", CacheAlgorithms.createFIFOMap(CACHE_MAX_SIZE)),
                    new ResearchCache("matchStatsCache", CacheAlgorithms.createFIFOMap(CACHE_MAX_SIZE))
            ));
            return fifoManager;
        } else if ("random".equalsIgnoreCase(cacheAlgorithm)) {
            SimpleCacheManager randomManager = new SimpleCacheManager();
            randomManager.setCaches(Arrays.asList(
                    new ResearchCache("wtaPlayersCache", CacheAlgorithms.createRandomMap(CACHE_MAX_SIZE)),
                    new ResearchCache("atpPlayersCache", CacheAlgorithms.createRandomMap(CACHE_MAX_SIZE)),
                    new ResearchCache("playerPhotosCache", CacheAlgorithms.createRandomMap(CACHE_MAX_SIZE)),
                    new ResearchCache("matchStatsCache", CacheAlgorithms.createRandomMap(CACHE_MAX_SIZE))
            ));
            return randomManager;

        }else if ("redis".equalsIgnoreCase(cacheAlgorithm)) {

            // 1. Domyślna konfiguracja z JSON-em
            RedisCacheConfiguration jsonConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                    .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                    .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

            // 2. Specjalna binarna konfiguracja dla zdjęć
            RedisCacheConfiguration photoConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                    .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));

            return RedisCacheManager.builder(redisConnectionFactory)
                    .cacheDefaults(jsonConfiguration) // Domyślnie używaj JSON-a
                    .withCacheConfiguration("playerPhotosCache", photoConfiguration) // dla zdjęć użyj binarnej konfiguracji
                    .build();
        }

        throw new IllegalArgumentException("Nieznany algorytm: " + cacheAlgorithm);
    }
}
