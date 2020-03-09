package com.igatec.mqlsloth.ci;

import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.ci.constants.SlothAdminType;

public class RoleCI extends UserCombinationCI {

    public RoleCI(String name) {
        this(name, CIDiffMode.TARGET);
    }

    public RoleCI(String name, CIDiffMode diffMode) {
        super(SlothAdminType.ROLE, name, diffMode);
    }

    @Override
    public RoleCI buildDiff(AbstractCI newCI) {
        RoleCI ci = (RoleCI) newCI;
        RoleCI diff = new RoleCI(getName(), CIDiffMode.DIFF);
        fillDiffCI(ci, diff);
        return diff;
    }

    @Override
    public AbstractCI buildDefaultCI(){
        return new RoleCI(getName());
    }

}
