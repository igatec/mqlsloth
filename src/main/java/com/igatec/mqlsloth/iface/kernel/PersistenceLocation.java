package com.igatec.mqlsloth.iface.kernel;

public interface PersistenceLocation {

    boolean isDatabase();
    boolean isFileSystem();
    String getRootDirectory();

}
