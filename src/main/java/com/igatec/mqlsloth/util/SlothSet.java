package com.igatec.mqlsloth.util;

import java.util.*;

public class SlothSet<E> extends HashSet<E> implements ReversibleSet<E> {

    private final static String MATCHING_EXCEPTION_TEXT = "Items and Reversed items have matching objects";

    private final Set<E> reversed = new HashSet<>();
    private final boolean diffMode;

    public SlothSet(boolean diffMode){
        super();
        this.diffMode = diffMode;
    }

    public SlothSet(Collection<E> items, boolean diffMode){
        this.diffMode = diffMode;
        super.addAll(items);
    }

    public SlothSet(ReversibleSet<E> reversibleSet, boolean diffMode){
        super();
        reversed.addAll(reversibleSet.getReversed());
        this.diffMode = diffMode;
        super.addAll(reversibleSet.get());
    }

    public SlothSet(Collection<E> items, Collection<E> reversedItems){
        for (E e:reversedItems) {
            if (items.contains(e))
                throw new ReversibleSetException(MATCHING_EXCEPTION_TEXT);
        }
        reversed.addAll(reversedItems);
        this.diffMode = true;
        super.addAll(items);
    }

    public boolean isDiffMode(){
        return diffMode;
    }

    @Override
    public boolean reverse(E e) {
        boolean r = remove(e);
        if (r) {
            return true;
        } else {
            return reversed.add(e);
        }
    }

    @Override
    public boolean add(E e){
        boolean r = reversed.remove(e);
        if (r) {
            return true;
        } else {
            return super.add(e);
        }
    }

    @Override
    public boolean reverseAll(Collection<? extends E> c) {
        boolean r = false;
        for (E e:c)
            r |= reverse(e);
        return r;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean r = false;
        for (E e:c)
            r |= add(e);
        return r;
    }

    @Override
    public boolean containsReversed(E e) {
        return reversed.contains(e);
    }

    @Override
    public boolean containsAllReversed(Collection<E> c) {
        return reversed.containsAll(c);
    }

    @Override
    public Iterator<E> iteratorOfReversed() {
        return reversed.iterator();
    }

    @Override
    public int sizeOfReversed() {
        return reversed.size();
    }

    @Override
    public boolean removeReversed(E e) {
        return reversed.remove(e);
    }

    @Override
    public boolean removeAllReversed(Collection<E> c) {
        return reversed.removeAll(c);
    }

    @Override
    public boolean retainAllReversed(Collection<E> c) {
        return reversed.retainAll(c);
    }

    @Override
    public Map<E, Boolean> toMap() {
        Map<E, Boolean> result = new HashMap<>();
        for (E e:this)
            result.put(e, true);
        for (E e:reversed)
            result.put(e, false);
        return result;
    }

    @Override
    public boolean isEmpty(){
        return super.isEmpty() && reversed.isEmpty();
    }

    @Override
    public void clearReversed(){
        reversed.clear();
    }

    @Override
    public void clearAll(){
        super.clear();
        reversed.clear();
    }

    @Override
    public Set<E> get() {
        return (Set<E>) super.clone();
    }

    @Override
    public Set<E> getReversed() {
        return new HashSet<>(reversed);
    }

    @Override
    public boolean shouldRemove(E e) {
        if (diffMode){
            return containsReversed(e);
        } else {
            return !contains(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SlothSet<?> slothSet = (SlothSet<?>) o;
        return Objects.equals(reversed, slothSet.reversed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), reversed);
    }

    public static <C> Set<C> itemsToRemove(ReversibleSet<C> state1, ReversibleSet<C> state2){
        HashSet<C> result = new HashSet<>();
        for (C c:state1){
            if (state2.shouldRemove(c))
                result.add(c);
        }
        return result;
    }

    public static <C> Set<C> itemsToAdd(ReversibleSet<C> state1, ReversibleSet<C> state2){
        HashSet<C> result = new HashSet<>();
        for (C c:state2){
            if (!state1.contains(c))
                result.add(c);
        }
        return result;
    }

}
