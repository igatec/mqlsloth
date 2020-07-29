package com.igatec.mqlsloth.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BiMap<K, V> {

    private Map<K, V> map = new HashMap<>();

    public void put(K key, V value) {
        map.put(key, value);
    }

    public V getValue(K key) {
        return map.get(key);
    }

    public K getKey(V value) {
        for (K key : map.keySet()) {
            if (value.equals(map.get(key))) {
                return key;
            }
        }
        return null;
    }

    public Set<K> keySet() {
        return map.keySet();
    }

    public Set<V> valueSet() {
        return new HashSet<>(map.values());
    }

    public int size() {
        return map.size();
    }

}
