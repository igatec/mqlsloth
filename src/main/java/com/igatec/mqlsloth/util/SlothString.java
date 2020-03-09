package com.igatec.mqlsloth.util;

import java.util.Objects;

public class SlothString implements ReversibleString {

    private final String value;

    public SlothString(String value){
        this.value = value;
    }

    public SlothString(){
        value = null;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public boolean isReversed() {
        return value == null;
    }

    @Override
    public String toString(){
        return value();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SlothString that = (SlothString) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

}
