package com.igatec.mqlsloth.script;

import com.igatec.mqlsloth.ci.util.CIFullName;

public class ModSymbolicNameChunk extends ScriptChunk {
    private final String ciType;
    private final String ciName;
    private final String symbolicName;

    public ModSymbolicNameChunk(CIFullName fullName, String symbolicName) {
        super(fullName);
        ciType = fullName.getAdminType().getMqlKey();
        ciName = fullName.getCIName().toString();
        this.symbolicName = symbolicName;
    }

    public String getCiType() {
        return ciType;
    }

    public String getCiName() {
        return ciName;
    }

    public String getSymbolicName() {
        return symbolicName;
    }

    @Override
    public String[] getCommand() {
        return new String[0];
    }
}
