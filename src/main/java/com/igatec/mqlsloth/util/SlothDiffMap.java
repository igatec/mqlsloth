package com.igatec.mqlsloth.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SlothDiffMap<V> extends HashMap<String, V> implements ReversibleMap<V> {

    public SlothDiffMap() {
        super();
    }

    public SlothDiffMap(Map<String, V> m) {
        super(m);
    }

    @Override
    public boolean shouldRemove(String key) {
        return containsKey(key) && get(key) == null;
    }

    @Override
    public ReversibleMap<V> clone() {
        return new SlothDiffMap<>(this);
    }

    public Set<String> keysToRemove() {
        return keySet().stream().filter(s -> get(s) == null).collect(Collectors.toCollection(HashSet::new));
    }

    public Map<String, V> mapToAdd() {
        Map<String, V> result = new HashMap<>();
        for (String key : keySet()) {
            V val = get(key);
            if (val != null)
                result.put(key, val);
        }
        return result;
    }

}
