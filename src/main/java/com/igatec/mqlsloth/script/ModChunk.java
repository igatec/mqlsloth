package com.igatec.mqlsloth.script;

import com.igatec.mqlsloth.ci.constants.ScriptPriority;
import com.igatec.mqlsloth.ci.constants.SlothAdminType;
import com.igatec.mqlsloth.ci.util.BusCIName;
import com.igatec.mqlsloth.ci.util.CIFullName;
import org.apache.commons.lang3.ArrayUtils;

public class ModChunk extends ScriptChunk implements AttachableChunk, HeadChunk {
    protected String[] cmdParam;
    protected boolean canBeAttached = true;

    public ModChunk(CIFullName relatedCI, String... cmdParam) {
        super(relatedCI);
        this.cmdParam = cmdParam;
        int prior = SlothAdminType.isBus(relatedCI.getAdminType()) ? ScriptPriority.SP_BUS_MODIFICATION : Integer.MIN_VALUE;
        setPriority(prior);
    }

    public ModChunk(CIFullName relatedCI, int priority, String... cmdParam) {
        this(relatedCI, cmdParam);
        int prior = SlothAdminType.isBus(relatedCI.getAdminType()) ? ScriptPriority.SP_BUS_MODIFICATION : priority;
        setPriority(prior);
    }

    @Override
    public boolean canBeAttached() {
        return canBeAttached;
    }

    public void setCanBeAttached(boolean canBeAttached) {
        this.canBeAttached = canBeAttached;
    }

    @Override
    public String[] getCommand() {
        return ArrayUtils.addAll(getCommandHead(), getCommandParam());
    }

    @Override
    public String[] getCommandHead() {
        CIFullName ci = getRelatedCI();
        SlothAdminType aType = ci.getAdminType();
        String mqlKey = aType.getMqlKey();
        if (SlothAdminType.isBus(aType)) {
            BusCIName tnr = (BusCIName) ci.getCIName();
            return new String[]{
                    MqlKeywords.M_MODIFY,
                    mqlKey,
                    MqlUtil.qWrap(tnr.getType()),
                    MqlUtil.qWrap(tnr.getName()),
                    MqlUtil.qWrap(tnr.getRevision())
            };
        } else {
            return new String[]{MqlKeywords.M_MODIFY, mqlKey, MqlUtil.qWrap(ci.getName())};
        }
    }

    @Override
    public String[] getCommandParam() {
        return cmdParam;
    }
}
