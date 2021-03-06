package com.igatec.mqlsloth.script;

import com.igatec.mqlsloth.ci.constants.AttributeType;
import com.igatec.mqlsloth.ci.util.CIFullName;
import org.apache.commons.lang3.ArrayUtils;

public class FormCreateChunk extends CreateChunk {

    private AttributeType type;

    public FormCreateChunk(CIFullName relatedCI) {
        super(relatedCI);
    }

    @Override
    public String[] getCommand() {
        return ArrayUtils.addAll(super.getCommand(), "web");
    }
}
