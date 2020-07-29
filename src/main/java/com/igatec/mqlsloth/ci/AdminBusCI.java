package com.igatec.mqlsloth.ci;

import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.ci.constants.SlothAdminType;
import com.igatec.mqlsloth.ci.util.BusCIName;

public abstract class AdminBusCI extends AbstractBusCI {
    public static final String OBJECT_GENERATOR_POLICY = "eService Object Generator";
    private static final String E_SERVICE_ADMINISTRATION_VAULT = "eService Administration";

    public AdminBusCI(SlothAdminType aType, BusCIName ciName, String policy, CIDiffMode diffMode) {
        super(aType, ciName, E_SERVICE_ADMINISTRATION_VAULT, policy, diffMode);
    }
}
