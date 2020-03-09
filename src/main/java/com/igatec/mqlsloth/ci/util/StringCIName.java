package com.igatec.mqlsloth.ci.util;

import java.util.Objects;

public class StringCIName extends AbstractCIName {

    private final String name;

    public StringCIName(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringCIName that = (StringCIName) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public int compareTo(AbstractCIName o) {
        int c = super.compareTo(o);
        if (c != 0)
            return c;
        StringCIName that = (StringCIName) o;
        return name.compareTo(that.name);
    }
}
