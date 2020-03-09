package com.igatec.mqlsloth.script.action;

import com.igatec.mqlsloth.script.MqlAction;

public class ModSymbolicNameAction implements MqlAction {

    private final String ciType;
    private final String ciName;
    private final String symbolicName;

    public ModSymbolicNameAction(String ciType, String ciName, String symbolicName){
        this.ciType = ciType;
        this.ciName = ciName;
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
}
