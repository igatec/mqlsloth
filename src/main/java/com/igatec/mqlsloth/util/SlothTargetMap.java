package com.igatec.mqlsloth.util;

import java.util.HashMap;
import java.util.Map;

public class SlothTargetMap<V> extends HashMap<String, V> implements ReversibleMap<V> {

    public SlothTargetMap() {
        super();
    }

    public SlothTargetMap(Map<String, V> m) {
        super(m);
    }

    @Override
    public boolean shouldRemove(String key) {
        return !this.containsKey(key);
    }

    @Override
    public ReversibleMap<V> clone() {
        return new SlothTargetMap<>(this);
    }
}
