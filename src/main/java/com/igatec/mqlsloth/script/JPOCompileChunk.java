package com.igatec.mqlsloth.script;

import com.igatec.mqlsloth.ci.ProgramCI;

public class JPOCompileChunk extends ScriptChunk {

    private final String name; // Without '_mxJPO'
    private final String code;

    public JPOCompileChunk(ProgramCI ci) {
        super(ci.getCIFullName());
        name = ci.getName();
        code = ci.getCode();
    }

    @Override
    public String[] getCommand() {
        return null;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}
