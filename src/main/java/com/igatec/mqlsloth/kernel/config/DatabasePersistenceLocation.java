package com.igatec.mqlsloth.kernel.config;

import com.igatec.mqlsloth.iface.kernel.PersistenceLocation;

public class DatabasePersistenceLocation implements PersistenceLocation {

    @Override
    public boolean isDatabase() {
        return true;
    }

    @Override
    public boolean isFileSystem() {
        return false;
    }

    @Override
    public String getRootDirectory() {
        return null;
    }
}
