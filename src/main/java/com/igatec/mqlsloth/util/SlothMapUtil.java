package com.igatec.mqlsloth.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class SlothMapUtil {

    public static <V> Set<String> keysToRemove(ReversibleMap<V> state1, ReversibleMap<V> state2){
        Set<String> result = new HashSet<>();
        for (String oldKey:state1.keySet()){
            if (state2.shouldRemove(oldKey) && !state1.shouldRemove(oldKey))
                result.add(oldKey);
        }
        return result;
    }

    public static <V> Map<String, V> mapToAdd(ReversibleMap<V> state1, ReversibleMap<V> state2){
        Map<String, V> result = new HashMap<>();
        for (String newKey:state2.keySet()){
            V newValue = state2.get(newKey);
            if (newValue != null && !newValue.equals(state1.get(newKey)))
                result.put(newKey, newValue);
        }
        return result;
    }

}
