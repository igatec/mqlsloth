package com.igatec.mqlsloth.iface.kernel;

public enum ExecutionState {

    BUILDING_DIFF ("Building source-target difference"),
    EXECUTING_SCRIPT ("Executing update script"),
    FINISHED ("Finished");

    private final String label;

    ExecutionState(String label){
        this.label = label;
    }

    @Override
    public String toString(){
        return label;
    }

}
