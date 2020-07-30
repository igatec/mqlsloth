package com.igatec.mqlsloth.framework;

import com.igatec.mqlsloth.kernel.SlothException;

import java.util.List;

public class MQLCommandImpl implements MQLCommand {

    private final static MQLCommand INSTANCE = new MQLCommandImpl();

    public static MQLCommand instance() {
        return INSTANCE;
    }

    private MQLCommandImpl() {
    }

    @Override
    public String executeOrThrow(Context context, String cmd, List<String> args) throws SlothException {
        executeCommand(context, cmd, args.toArray(new String[]{}));
        close(context);
        String error = getError();
        if (error == null || error.equals("")) {
            return getResult();
        } else {
            throw new SlothException(error);
        }
    }

    private void executeCommand(Context context, String cmd, String... args) {
        // TODO Reflection call should be here
    }

    private void close(Context context) {
        // TODO Reflection call should be here
    }

    private String getError() {
        // TODO Reflection call should be here
        return null;
    }

    private String getResult() {
        // TODO Reflection call should be here
        return null;
    }
}
