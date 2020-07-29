package com.igatec.mqlsloth.ci.util;

import com.igatec.mqlsloth.ci.constants.SlothAdminType;

import java.util.Comparator;
import java.util.Objects;

public class CIFullName implements Comparable<CIFullName> {

    private SlothAdminType adminType;
    private AbstractCIName ciName;
    private final Comparator<SlothAdminType> slothAdminTypeComparator;

    public CIFullName(SlothAdminType adminType, AbstractCIName ciName) {
        this.adminType = adminType;
        this.ciName = ciName;
        slothAdminTypeComparator = new SlothAdminType.Comp();
    }

    public SlothAdminType getAdminType() {
        return adminType;
    }

    public String getName() {
        return ciName.toString();
    }

    public AbstractCIName getCIName() {
        return ciName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CIFullName fullName = (CIFullName) o;
        return adminType == fullName.adminType
                && Objects.equals(ciName, fullName.ciName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(adminType, ciName);
    }

    @Override
    public String toString() {
        return adminType + "_" + ciName;
    }

    @Override
    public int compareTo(CIFullName o) {
        int c = slothAdminTypeComparator.compare(adminType, o.adminType);
        if (c != 0) {
            return c;
        }
        return ciName.compareTo(o.ciName);
    }

}
