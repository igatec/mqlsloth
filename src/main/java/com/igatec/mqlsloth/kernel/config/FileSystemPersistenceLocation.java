package com.igatec.mqlsloth.kernel.config;

import com.igatec.mqlsloth.iface.kernel.PersistenceLocation;

public class FileSystemPersistenceLocation implements PersistenceLocation {

    private final String directory;

    public FileSystemPersistenceLocation(String directory) {
        this.directory = directory;
    }

    @Override
    public boolean isDatabase() {
        return false;
    }

    @Override
    public boolean isFileSystem() {
        return true;
    }

    @Override
    public String getRootDirectory() {
        return directory;
    }
}
