package com.igatec.mqlsloth.util;

public class EndOfCollectionStub<T> extends ObjectContainer<T> {
    @Override
    public boolean isStub() {
        return true;
    }
}
