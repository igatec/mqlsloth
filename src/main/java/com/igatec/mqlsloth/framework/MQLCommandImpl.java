package com.igatec.mqlsloth.framework;

import com.igatec.mqlsloth.kernel.SlothException;

import java.util.List;

public class MQLCommandImpl implements MQLCommand {

    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public String executeOrThrow(String cmd, List<String> args) throws SlothException {
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
