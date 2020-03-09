package com.igatec.mqlsloth.script;

import com.igatec.mqlsloth.ci.constants.AttributeType;
import com.igatec.mqlsloth.ci.util.CIFullName;
import org.apache.commons.lang3.ArrayUtils;

public class AttributeCreateChunk extends CreateChunk {

    private AttributeType type;

    public AttributeCreateChunk(CIFullName relatedCI, AttributeType type) {
        super(relatedCI);
        this.type = type;
    }

    @Override
    public String[] getCommand() {
        return ArrayUtils.addAll(super.getCommand(), M_ATTRIBUTE_TYPE, type.getMqlKeyword());
    }

}
