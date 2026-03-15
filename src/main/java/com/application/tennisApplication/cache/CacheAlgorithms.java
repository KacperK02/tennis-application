package com.application.tennisApplication.cache;

import java.util.LinkedHashMap;
import java.util.Map;

public class CacheAlgorithms {

    // LRU
    public static Map<Object, Object> createLRUMap(int maxSize) {
        return new LinkedHashMap<Object, Object>(maxSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Object, Object> eldest) {
                System.out.println("[LRU] Pamięć pełna! Wyrzucam najdawniej używany klucz: " + eldest.getKey());
                return size() > maxSize;
            }
        };
    }

    // FIFO
    public static Map<Object, Object> createFIFOMap(int maxSize) {
        return new LinkedHashMap<Object, Object>(maxSize, 0.75f, false) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Object, Object> eldest) {
                System.out.println("[FIFO] Pamięć pełna! Wyrzucam najstarszy dodany klucz: " + eldest.getKey());
                return size() > maxSize;
            }
        };
    }
}
