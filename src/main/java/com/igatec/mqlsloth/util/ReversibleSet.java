package com.igatec.mqlsloth.util;

import java.util.*;

public interface ReversibleSet<E> extends Set<E> {

    boolean reverse(E e);
    boolean reverseAll(Collection<? extends E> c);
    boolean containsReversed(E e);
    boolean containsAllReversed(Collection<E> c);
    Iterator<E> iteratorOfReversed();
    int sizeOfReversed();
    boolean removeReversed(E e);
    boolean removeAllReversed(Collection<E> c);
    boolean retainAllReversed(Collection<E> c);
    Map<E, Boolean> toMap();
    void clearReversed();
    void clearAll();
    Set<E> get();
    Set<E> getReversed();
    boolean shouldRemove(E e);

}
