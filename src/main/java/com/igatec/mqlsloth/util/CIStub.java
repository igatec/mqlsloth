package com.igatec.mqlsloth.util;

import com.igatec.mqlsloth.ci.AbstractCI;
import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.ci.constants.SlothAdminType;
import com.igatec.mqlsloth.ci.util.StringCIName;

public class CIStub extends AbstractCI {
    public CIStub() {
        super(SlothAdminType.TYPE, new StringCIName(""), CIDiffMode.TARGET);
    }

    @Override
    public AbstractCI buildDiff(AbstractCI newCI) {
        return null;
    }

    @Override
    public AbstractCI buildDefaultCI() {
        return null;
    }
}
