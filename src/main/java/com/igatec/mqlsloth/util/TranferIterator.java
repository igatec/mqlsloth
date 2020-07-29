package com.igatec.mqlsloth.util;

import java.util.Iterator;
import java.util.function.Function;

public class TranferIterator<K, V> implements Iterator<V> {

    private final Iterator<K> keyIterator;
    private final Function<K, V> produser;

    public TranferIterator(Iterator<K> keyIterator, Function<K, V> produser) {
        this.keyIterator = keyIterator;
        this.produser = produser;
    }

    @Override
    public boolean hasNext() {
        return keyIterator.hasNext();
    }

    @Override
    public V next() {
        return produser.apply(keyIterator.next());
    }

}
