package com.igatec.mqlsloth.script;

import com.igatec.mqlsloth.ci.constants.ScriptPriority;
import com.igatec.mqlsloth.ci.util.BusCIName;
import com.igatec.mqlsloth.ci.util.CIFullName;

import java.util.Arrays;

public class ModBusChunk extends ScriptChunk implements MqlKeywords {

    private final String[] cmd;

    // CHECKSTYLE.OFF: MagicNumber
    public ModBusChunk(CIFullName relatedCI, String... params) {
        super(relatedCI);
        cmd = new String[5 + params.length];
        BusCIName tnr = (BusCIName) relatedCI.getCIName();
        cmd[0] = M_MODIFY;
        cmd[1] = M_BUS;
        cmd[2] = MqlUtil.qWrap(tnr.getType());
        cmd[3] = MqlUtil.qWrap(tnr.getName());
        cmd[4] = MqlUtil.qWrap(tnr.getRevision());
        int i = 5;
        for (String p : params) {
            cmd[i++] = MqlUtil.qWrap(p);
        }
        setPriority(ScriptPriority.SP_BUS_MODIFICATION);
    }
    // CHECKSTYLE.ON: MagicNumber

    @Override
    public String[] getCommand() {
        return Arrays.copyOf(cmd, cmd.length);
    }
}
