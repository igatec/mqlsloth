package com.igatec.mqlsloth.script;

import com.igatec.mqlsloth.ci.util.CIFullName;

public class CreateChunk extends ScriptChunk implements HeadChunk, CreatingChunk, MqlKeywords {

    public CreateChunk(CIFullName relatedCI){
        super(relatedCI);
        setPriority(Integer.MIN_VALUE);
    }

    @Override
    public String[] getCommand() {
        CIFullName ci = getRelatedCI();
        return new String[]{M_ADD, ci.getAdminType().getMqlKey(), MqlUtil.qWrap(ci.getName())};
    }


    @Override
    public String[] getCommandHead() {
        return getCommand();
    }
}
