package com.igatec.mqlsloth.script;

import com.igatec.mqlsloth.ci.constants.ScriptPriority;
import com.igatec.mqlsloth.ci.constants.SlothAdminType;
import com.igatec.mqlsloth.ci.util.BusCIName;
import com.igatec.mqlsloth.ci.util.CIFullName;

import java.util.Arrays;

public class ConnectBusChunk extends ScriptChunk {

    private final String[] command;

    // CHECKSTYLE.OFF: MagicNumber
    public ConnectBusChunk(BusCIName from, String relName, BusCIName to, boolean connect) {
        super(new CIFullName(SlothAdminType.BUS, from));
        command = new String[11];
        command[0] = connect ? "connect" : "disconnect";
        command[1] = "bus";
        command[2] = MqlUtil.qWrap(from.getType());
        command[3] = MqlUtil.qWrap(from.getName());
        command[4] = MqlUtil.qWrap(from.getRevision());
        command[5] = "rel";
        command[6] = MqlUtil.qWrap(relName);
        command[7] = "to";
        command[8] = MqlUtil.qWrap(to.getType());
        command[9] = MqlUtil.qWrap(to.getName());
        command[10] = MqlUtil.qWrap(to.getRevision());
        setPriority(ScriptPriority.SP_BUS_MODIFICATION);
    }
    // CHECKSTYLE.ON: MagicNumber

    @Override
    public String[] getCommand() {
        return Arrays.copyOf(command, command.length);
    }

}
