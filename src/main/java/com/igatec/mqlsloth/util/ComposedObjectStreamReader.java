package com.igatec.mqlsloth.util;

import com.igatec.mqlsloth.kernel.SlothException;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class ComposedObjectStreamReader<C> implements ObjectStreamReader<C> {

    private final Iterator<ObjectStreamReader<C>> readersIter;
    private ObjectStreamReader<C> currentReader;
    private C next;

    public ComposedObjectStreamReader(List<ObjectStreamReader<C>> readers) throws SlothException {
        readersIter = readers.iterator();
        currentReader = readersIter.next();
        init();
    }

    private void init() throws SlothException {
        next = null;
        while (!currentReader.hasNext()) {
            if (readersIter.hasNext()) {
                currentReader = readersIter.next();
            } else {
                return;
            }
        }
        next = currentReader.next();
    }

    public C next() throws SlothException {
        C next = this.next;
        if (next == null) {
            throw new NoSuchElementException();
        }
        init();
        return next;
    }

    public boolean hasNext() {
        return next != null;
    }

}
