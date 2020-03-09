package com.igatec.mqlsloth.script;

import com.igatec.mqlsloth.ci.util.CIFullName;

public class DeleteChunk extends ScriptChunk implements DeletingChunk, MqlKeywords {
    public DeleteChunk(CIFullName relatedCI) {
        super(relatedCI);
    }

    @Override
    public String[] getCommand() {
        CIFullName ci = getRelatedCI();
        return new String[]{M_DELETE, ci.getAdminType().getMqlKey(), MqlUtil.qWrap(ci.getName())};
    }
}
