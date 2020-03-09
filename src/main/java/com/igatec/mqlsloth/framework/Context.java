package com.igatec.mqlsloth.framework;

public interface Context {
    static Context instance(String host) {
        return new Context() {
            @Override
            public void setUser(String user) {
                // todo
            }

            @Override
            public void setPassword(String password) {
                // todo
            }

            @Override
            public void connect() {
                // todo
            }

            @Override
            public boolean isConnected() {
                // todo
                return false;
            }

            @Override
            public String createWorkspace() {
                // todo
                return null;
            }
        };
    }

    void setUser(String user);

    void setPassword(String password);

    void connect();

    boolean isConnected();

    String createWorkspace();
}
