package com.igatec.mqlsloth.ci;

import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.ci.constants.SlothAdminType;
import com.igatec.mqlsloth.ci.util.BusCIName;
import com.igatec.mqlsloth.script.ScriptChunk;
import com.igatec.mqlsloth.util.ReversibleMap;
import com.igatec.mqlsloth.util.SlothDiffMap;
import com.igatec.mqlsloth.util.SlothTargetMap;

import java.util.List;

public abstract class AdminBusCI extends AbstractBusCI {

    private final static String E_SERVICE_ADMINISTRATION_VAULT = "eService Administration";
    public final static String OBJECT_GENERATOR_POLICY = "eService Object Generator";

    public AdminBusCI(SlothAdminType aType, BusCIName ciName, String policy, CIDiffMode diffMode) {
        super(aType, ciName, E_SERVICE_ADMINISTRATION_VAULT, policy, diffMode);
    }





}
