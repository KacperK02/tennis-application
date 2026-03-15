package com.application.tennisApplication.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.CacheManager;

import java.util.Arrays;

@Configuration
@EnableCaching
public class CacheResearchConfig {
    // Nowa flaga w application.properties (np. caffeine, lru, fifo)
    @Value("${app.cache.algorithm:caffeine}")
    private String cacheAlgorithm;

    @Bean
    public CacheManager cacheManager() {
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
            // KLASYCZNE LRU
            SimpleCacheManager lruManager = new SimpleCacheManager();
            lruManager.setCaches(Arrays.asList(
                    new ResearchCache("wtaPlayersCache", CacheAlgorithms.createLRUMap(CACHE_MAX_SIZE)),
                    new ResearchCache("atpPlayersCache", CacheAlgorithms.createLRUMap(CACHE_MAX_SIZE)),
                    new ResearchCache("playerPhotosCache", CacheAlgorithms.createLRUMap(CACHE_MAX_SIZE)),
                    new ResearchCache("matchStatsCache", CacheAlgorithms.createLRUMap(CACHE_MAX_SIZE))
            ));
            return lruManager;

        } else if ("fifo".equalsIgnoreCase(cacheAlgorithm)) {
            // KLASYCZNE FIFO
            SimpleCacheManager fifoManager = new SimpleCacheManager();
            fifoManager.setCaches(Arrays.asList(
                    new ResearchCache("wtaPlayersCache", CacheAlgorithms.createFIFOMap(CACHE_MAX_SIZE)),
                    new ResearchCache("atpPlayersCache", CacheAlgorithms.createFIFOMap(CACHE_MAX_SIZE)),
                    new ResearchCache("playerPhotosCache", CacheAlgorithms.createFIFOMap(CACHE_MAX_SIZE)),
                    new ResearchCache("matchStatsCache", CacheAlgorithms.createFIFOMap(CACHE_MAX_SIZE))
            ));
            return fifoManager;
        }

        throw new IllegalArgumentException("Nieznany algorytm: " + cacheAlgorithm);
    }
}
