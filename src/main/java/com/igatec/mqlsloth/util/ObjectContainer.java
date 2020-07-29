package com.igatec.mqlsloth.util;

public class ObjectContainer<T> {

    private T value = null;
    private Exception ex = null;

    public ObjectContainer() {
    }

    public ObjectContainer(T object) {
        this.value = object;
    }

    public ObjectContainer(Exception ex) {
        this.ex = ex;
    }

    public T value() {
        return value;
    }

    public boolean isStub() {
        return false;
    }

    public Exception getException() {
        return ex;
    }

}
