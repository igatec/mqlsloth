package com.igatec.mqlsloth.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class ObjectStreamReaderImpl<C> implements ObjectStreamReader<C> {
    private Iterator<C> iterator;

    public ObjectStreamReaderImpl(C... items) {
        iterator = Arrays.asList(items).iterator();
    }

    public ObjectStreamReaderImpl(Collection<C> c) {
        iterator = c.iterator();
    }

    public C next() {
        return iterator.next();
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }
}
