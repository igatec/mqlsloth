package com.igatec.mqlsloth.script.action;

import com.igatec.mqlsloth.script.MqlAction;

public class JPOCompileAction implements MqlAction {

    private final String progName; // Without '_mxJPO'
    private final String code;

    public JPOCompileAction(String progName, String code) {
        this.progName = progName;
        this.code = code;
    }

    public String getProgName() {
        return progName;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "Compile java program '" + progName + "'";
    }
}
