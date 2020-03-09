package com.igatec.mqlsloth.kernel.session;

import com.igatec.mqlsloth.iface.kernel.PersistenceLocation;
import com.igatec.mqlsloth.iface.kernel.SearchLocation;
import com.igatec.mqlsloth.kernel.config.DatabasePersistenceLocation;
import com.igatec.mqlsloth.kernel.config.FileSystemPersistenceLocation;

public abstract class AbstractSessionBuilder extends SessionWithContextBuilder {

    protected PersistenceLocation sourceLocation;
    protected PersistenceLocation targetLocation;
    protected SearchLocation searchLocation = SearchLocation.TARGET;
    protected String[] searchPattern;
    protected boolean syncronous = false;

    public void setSyncronous(boolean syncronous) {
        this.syncronous = syncronous;
    }

    public void setSource(PersistenceLocation location) {
        this.sourceLocation = location;
    }

    public void setDatabaseAsSource() {
        this.sourceLocation = new DatabasePersistenceLocation();
    }

    public void setFileSystemAsSource(String directory) {
        this.sourceLocation = new FileSystemPersistenceLocation(directory);
    }

    public void setTarget(PersistenceLocation location) {
        this.targetLocation = location;
    }

    public void setDatabaseAsTarget() {
        this.targetLocation = new DatabasePersistenceLocation();
    }

    public void setFileSystemAsTarget(String directory) {
        this.targetLocation = new FileSystemPersistenceLocation(directory);
    }

    public void setSearchLocation(SearchLocation location) {
        this.searchLocation = location;
    }

    public void setSearchPattern(String[] searchPattern) {
        this.searchPattern = searchPattern;
    }

}
