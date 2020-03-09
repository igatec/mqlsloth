package com.igatec.mqlsloth.util;

import com.igatec.mqlsloth.framework.Context;
import com.igatec.mqlsloth.iface.kernel.Session;
import com.igatec.mqlsloth.kernel.SlothException;
import com.igatec.mqlsloth.kernel.session.SlothApp;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Workspace {

    private final File dir;
    private static final Map<Integer, Map<UUID, Workspace>> workspaces = new HashMap<>();

    private Workspace(String dir) {
        this.dir = new File(dir);
    }

    public static void deleteAll() {
        deleteAll(SlothApp.getCurrentSession());
    }

    public static Workspace create() throws SlothException {
        return create(SlothApp.getCurrentSession());
    }

    public static void deleteAll(Session session) {
        int sessionHash = session.hashCode();
        Map<UUID, Workspace> wss = workspaces.get(sessionHash);
        if (wss != null) {
            for (Workspace ws : wss.values()) {
                ws.delete();
            }
        }
        workspaces.remove(sessionHash);
    }

    public static Workspace create(Session session) throws SlothException {
        Context context = session.getContext();
        String dirName = null;
        UUID key = UUID.randomUUID();
        try {
            dirName = context.createWorkspace() + File.separator + key;
            new File(dirName).mkdir();
        } catch (Exception e) {
            throw new SlothException("Could not create workspace", e);
        }
        Workspace ws = new Workspace(dirName);
        int sessionHash = session.hashCode();
        if (!workspaces.containsKey(sessionHash)) {
            workspaces.put(sessionHash, new HashMap<>());
        }
        workspaces.get(sessionHash).put(key, ws);
        return ws;
    }

    public File getDir() {
        return dir;
    }

    private void delete() {
        try {
            if (dir.exists())
                FileUtils.cleanDirectory(dir);
        } catch (IOException e) {
        }
        FileUtils.deleteQuietly(dir);
    }


}
