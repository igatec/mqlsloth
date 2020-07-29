package com.igatec.mqlsloth.script;

import com.igatec.mqlsloth.ci.constants.ScriptPriority;
import com.igatec.mqlsloth.ci.util.BusCIName;
import com.igatec.mqlsloth.ci.util.CIFullName;

import java.util.Arrays;

public class CreateBusChunk extends ScriptChunk implements MqlKeywords {

    private final String[] cmd;

    // CHECKSTYLE.OFF: MagicNumber
    public CreateBusChunk(CIFullName relatedCI, String policy, String vault) {
        super(relatedCI);
        cmd = new String[9];
        BusCIName tnr = (BusCIName) relatedCI.getCIName();
        cmd[0] = M_ADD;
        cmd[1] = M_BUS;
        cmd[2] = MqlUtil.qWrap(tnr.getType());
        cmd[3] = MqlUtil.qWrap(tnr.getName());
        cmd[4] = MqlUtil.qWrap(tnr.getRevision());
        cmd[5] = M_POLICY;
        cmd[6] = MqlUtil.qWrap(policy);
        cmd[7] = M_VAULT;
        cmd[8] = MqlUtil.qWrap(vault);
        setPriority(ScriptPriority.SP_BUS_CREATION);
    }
    // CHECKSTYLE.ON: MagicNumber

    @Override
    public String[] getCommand() {
        return Arrays.copyOf(cmd, cmd.length);
    }
}
