package com.igatec.mqlsloth.kernel.config;

public class LocalExecutionLocation extends ExecutionLocation {

    public static final String NAME = "__LOCAL__";

    @Override
    public boolean isLocal() {
        return true;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
