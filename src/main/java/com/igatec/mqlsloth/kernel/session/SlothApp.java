package com.igatec.mqlsloth.kernel.session;

//import com.igatec.mqlsloth.kernel.session.DiffSessionBuilder;

import com.igatec.mqlsloth.iface.kernel.Session;

import java.util.HashMap;
import java.util.Map;

public abstract class SlothApp {

    private static Map<Thread, Session> runningSessions = new HashMap<>();

    public static ExportSessionBuilder getExportSessionBuilder() {
        return new ExportSessionBuilder();
    }

    public static DiffSessionBuilder getDiffSessionBuilder() {
        return new DiffSessionBuilder();
    }

    static synchronized void registerSession(Session session) {
        Thread t = Thread.currentThread();
        if (runningSessions.containsKey(t))
            throw new RuntimeException("Session is already registered");
        runningSessions.put(t, session);
    }

    public static Session getCurrentSession() {
        return runningSessions.get(Thread.currentThread());
    }

    public static void unregisterAllSessions() {
        runningSessions.clear();
    }

    public static void unregisterSession() {
        runningSessions.remove(Thread.currentThread());
    }

}
