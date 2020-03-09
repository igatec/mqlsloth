package com.igatec.mqlsloth.kernel.session;

import com.igatec.mqlsloth.iface.kernel.DiffSessionConfig;
import com.igatec.mqlsloth.kernel.SlothException;
import com.igatec.mqlsloth.kernel.config.FileSystemPersistenceLocation;

public class DiffSessionBuilder extends AbstractSessionBuilder {

    protected FileSystemPersistenceLocation outputLocation;
    private boolean isImport = false;

    @Override
    public DiffSession build() throws SlothException {
        DiffSession session = new DiffSession(context,
                sourceLocation, targetLocation, outputLocation,
                searchLocation, searchPattern,
                isImport ? new DefaultImportSessionConfig() : new DefaultDiffSessionConfig(),
                syncronous
        );
        SlothApp.registerSession(session);
        return session;
    }

    public void setFileSystemAsOutput(String directory){
        this.outputLocation = new FileSystemPersistenceLocation(directory);
    }

    public void setOutputLocation(String dir){
        outputLocation = new FileSystemPersistenceLocation(dir);
    }

    public void setIsImport(boolean isImport){
        this.isImport = isImport;
    }

}

class DefaultDiffSessionConfig extends DefaultConfig {
    @Override
    public boolean shouldExecuteUpdateScript() {
        return false;
    }
}

class DefaultImportSessionConfig extends DefaultConfig {
    @Override
    public boolean shouldExecuteUpdateScript() {
        return true;
    }
}

abstract class DefaultConfig implements DiffSessionConfig {

    @Override
    public boolean shouldSaveUpdateScript() {
        return true;
    }

    @Override
    public boolean shouldSaveDiff() {
        return true;
    }

    @Override
    public boolean executeInTransaction() {
        return true;
    }
}
