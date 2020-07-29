package com.igatec.mqlsloth.ci.util;

import java.util.Objects;

public class BusCIName extends AbstractCIName {

    private final String type;
    private final String name;
    private final String revision;

    public BusCIName(String type, String name, String revision) {
        this.type = type;
        this.name = name;
        this.revision = revision;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getRevision() {
        return revision;
    }

    @Override
    public String toString() {
        return String.format("'%s' '%s' '%s'", type, name, revision);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BusCIName busCIName = (BusCIName) o;
        return Objects.equals(type, busCIName.type)
                && Objects.equals(name, busCIName.name)
                && Objects.equals(revision, busCIName.revision);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name, revision);
    }

    @Override
    public int compareTo(AbstractCIName o) {
        int c = super.compareTo(o);
        if (c != 0) {
            return c;
        }
        BusCIName that = (BusCIName) o;
        c = type.compareTo(that.type);
        if (c != 0) {
            return c;
        }
        c = name.compareTo(that.name);
        if (c != 0) {
            return c;
        }
        return revision.compareTo(that.revision);
    }

}
