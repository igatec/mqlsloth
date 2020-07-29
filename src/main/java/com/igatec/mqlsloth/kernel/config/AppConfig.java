package com.igatec.mqlsloth.kernel.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.igatec.mqlsloth.kernel.SlothException;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class AppConfig {

    public static AppConfig buildFromFile(File configFile) throws SlothException {
        AppConfig instance;
        try {
            instance = new ObjectMapper().readValue(configFile, AppConfig.class);
            return instance;
        } catch (IOException e) {
            throw new SlothException("Could not read configuration file", e);
        }
    }

    private List<RemoteConfig> remotes = new LinkedList<>();

    public void addRemote(RemoteConfig remote) {
        remotes.add(remote);
    }

    public RemoteConfig getRemoteByName(String name) {
        RemoteConfig result = null;
        for (RemoteConfig r : remotes) {
            if (name.equals(r.getName())) {
                result = r;
            }
        }
        return result;
    }

    public void setRemotes(List<RemoteConfig> remotes) {
        this.remotes = new LinkedList<>(remotes);
    }

    public List<RemoteConfig> getRemotes() {
        return new LinkedList<>(remotes);
    }

    public boolean validate() {
        for (RemoteConfig rConf : remotes) {
            if (!rConf.validate()) {
                return false;
            }
        }
        return true;
    }

}
