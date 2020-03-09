package com.igatec.mqlsloth.kernel.config;

public class RemoteExecutionLocation extends ExecutionLocation {

    private final RemoteConfig remoteConfig;

    public RemoteExecutionLocation(RemoteConfig remoteConfig){
        this.remoteConfig = remoteConfig;
    }

    public RemoteConfig getRemoteConfig() {
        return remoteConfig;
    }

    @Override
    public boolean isLocal() {
        return false;
    }

    @Override
    public String getName() {
        return remoteConfig.getName();
    }
}
