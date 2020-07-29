package com.igatec.mqlsloth.kernel.config;

public abstract class ExecutionLocation {
    public abstract boolean isLocal();

    public abstract String getName();
}
