package com.igatec.mqlsloth.util;

import java.util.Map;

public interface ReversibleMap<V> extends Map<String, V> {
    boolean shouldRemove(String key);

    ReversibleMap<V> clone();
}
