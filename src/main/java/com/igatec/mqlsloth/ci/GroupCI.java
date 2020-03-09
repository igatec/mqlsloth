package com.igatec.mqlsloth.ci;

import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.ci.constants.SlothAdminType;

public class GroupCI extends UserCombinationCI {

    public GroupCI(String name) {
        this(name, CIDiffMode.TARGET);
    }

    public GroupCI(String name, CIDiffMode diffMode) {
        super(SlothAdminType.GROUP, name, diffMode);
    }

    @Override
    public GroupCI buildDiff(AbstractCI newCI) {
        GroupCI ci = (GroupCI) newCI;
        GroupCI diff = new GroupCI(getName(), CIDiffMode.DIFF);
        fillDiffCI(ci, diff);
        return diff;
    }

    @Override
    public AbstractCI buildDefaultCI(){
        return new GroupCI(getName());
    }

}
