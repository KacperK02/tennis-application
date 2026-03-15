package com.application.tennisApplication.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;

public class ResearchCache implements Cache {
    private final String name;
    private final Map<Object, Object> store;

    public ResearchCache(String name, Map<Object, Object> store) {
        this.name = name;
        this.store = Collections.synchronizedMap(store);
    }

    @Override
    public String getName() { return name; }

    @Override
    public Object getNativeCache() { return store; }

    @Override
    public Cache.ValueWrapper get(Object key) {
        Object value = store.get(key);
        return (value != null ? new SimpleValueWrapper(value) : null);
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        return (T) store.get(key);
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        synchronized (store) {
            Object value = store.get(key);
            if (value != null) {
                return (T) value;
            }
            try {
                T newValue = valueLoader.call();
                store.put(key, newValue);
                return newValue;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void put(Object key, Object value) { store.put(key, value); }

    @Override
    public void evict(Object key) { store.remove(key); }

    @Override
    public void clear() { store.clear(); }
}
