package com.application.tennisApplication.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

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

    public static Map<Object, Object> createRandomMap(int maxSize) {
        return new LinkedHashMap<Object, Object>(maxSize, 0.75f, false) {
            private final Random random = new Random();

            @Override
            protected boolean removeEldestEntry(Map.Entry<Object, Object> eldest) {
                if (size() > maxSize) {
                    Object[] keys = keySet().toArray();
                    Object randomKey = keys[random.nextInt(keys.length)];
                    System.out.println("[RANDOM] Pamięć pełna! Wyrzucam losowy klucz: " + randomKey);
                    remove(randomKey);
                    return false;
                }
                return false;
            }
        };
    }
}
