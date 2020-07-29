package com.igatec.mqlsloth.ci.util;

public abstract class AbstractCIName implements Comparable<AbstractCIName> {
    @Override
    public int compareTo(AbstractCIName o) {
        return this.getClass().getName().compareTo(o.getClass().getName());
    }
}
