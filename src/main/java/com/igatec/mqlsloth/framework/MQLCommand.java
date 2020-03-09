package com.igatec.mqlsloth.framework;

public interface MQLCommand {
    static MQLCommand instance() {
        return new MQLCommand() {
            @Override
            public void executeCommand(Context context, String cmd, String... args) {
                // todo
            }

            @Override
            public void close(Context context) {
                // todo
            }

            @Override
            public String getError() {
                // todo
                return null;
            }

            @Override
            public String getResult() {
                // todo
                return null;
            }
        };
    }

    void executeCommand(Context context, String cmd, String... args);

    void close(Context context);

    String getError();

    String getResult();
}