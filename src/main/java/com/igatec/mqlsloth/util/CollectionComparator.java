package com.igatec.mqlsloth.util;

import java.util.Comparator;
import java.util.Iterator;

public class CollectionComparator<T extends Comparable<T>> implements Comparator<Iterable<T>> {
    @Override
    public int compare(Iterable<T> o1, Iterable<T> o2) {
        Iterator<T> it2 = o2.iterator();
        for (T i1 : o1) {
            if (!it2.hasNext()) {
                return 1;
            }
            T i2 = it2.next();
            int c = i1.compareTo(i2);
            if (c != 0) {
                return c;
            }
        }
        if (!it2.hasNext()) {
            return 0;
        }
        return -1;
    }
}
