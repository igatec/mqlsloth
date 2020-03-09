package com.igatec.mqlsloth.util;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DiffList<E> extends AbstractList<E> {

    private final List<E> list;
    private final List<Mode> modeList;
    private final Function<E,Object> identifier;

    /*
        Uniqueness of items is defined by identifier function
        Lists state1 and state2 must not contain duplicates
        All items in lists state1 and state2 must contain items in the same order
    */
    public DiffList(List<E> state1, List<E> state2,
                    Function<E,Object> identifier,
                    BiFunction<E,E,E> diffBuilder){

        this.identifier = identifier;
        list = new ArrayList<>(Math.max(state1.size(), state2.size()));
        modeList = new ArrayList<>(Math.max(state1.size(), state2.size()));

        List<Object> state1Order = state1.stream().map(identifier).collect(Collectors.toList());
        List<Object> state2Order = state2.stream().map(identifier).collect(Collectors.toList());

        /* Check for duplicates */
        Set<Object> checkSet = new HashSet<>();
        for (Object e:state1Order) {
            if (!checkSet.add(e))
                throw new IllegalArgumentException("List state1 contains duplicates");
        }
        checkSet.clear();
        for (Object e:state2Order) {
            if (!checkSet.add(e))
                throw new IllegalArgumentException("List state2 contains duplicates");
        }

        /* Check for order */
        int control = -1;
        for (Object item : state1Order) {
            int c = state2Order.indexOf(item);
            if (c != -1 && c < control)
                throw new IllegalArgumentException("Input lists sequence conflict");
            control = c;
        }

        /* Main code */
        for (int i=0; i<state1.size(); i++){
            E next1 = state1.get(i);
            Object next1Id = state1Order.get(i);
            int state2Index = state2Order.indexOf(next1Id);
            if (state2Index == -1){
                list.add(next1);
                modeList.add(Mode.REMOVE);
            } else {
                list.add(diffBuilder.apply(next1, state2.get(state2Index)));
                modeList.add(Mode.UPDATE);
            }
        }
        int i1 = -1;
        int created = 0;
        for (int i=0; i<state2.size(); i++){
            E next2 = state2.get(i);
            int c = state1Order.indexOf(state2Order.get(i));
            if (c != -1)
                i1 = c;
            else {
                list.add(i1+1+created, next2);
                modeList.add(i1+1+created, Mode.CREATE);
                created++;
            }
        }

    }

    public Mode getMode(int index){
        return modeList.get(index);
    }

    @Override
    public E get(int index) {
        return list.get(index);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean add(E item){
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends E> items){
        throw new UnsupportedOperationException();
    }

    public enum Mode {
        CREATE, UPDATE, REMOVE
    }

    @Override
    public String toString(){
        List<String> result = new LinkedList<>();
        for (int i=0; i<size(); i++){
            result.add(getMode(i) + ":" + identifier.apply(get(i)).toString());
        }
        return Arrays.toString(result.toArray());
    }

}
