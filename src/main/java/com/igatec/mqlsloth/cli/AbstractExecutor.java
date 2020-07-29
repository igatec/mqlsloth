package com.igatec.mqlsloth.cli;

import com.igatec.mqlsloth.framework.Context;
import com.igatec.mqlsloth.kernel.SlothException;
import com.igatec.mqlsloth.kernel.session.SessionWithContextBuilder;
import org.apache.commons.cli.CommandLine;

public abstract class AbstractExecutor implements CLIConstants {

    public static final int REQUIRED_NUMBER_OF_ARGS = 3;
    protected boolean verbose = false;
    protected boolean sync;
    protected final SlothAppCLI cli;

    public AbstractExecutor(SlothAppCLI cli) {
        this.cli = cli;
    }

    public abstract void run(Context context, CommandLine cmd) throws SlothException;

    void provideContext(Context context, CommandLine cmd, SessionWithContextBuilder sb) throws SlothException {
        if (cmd.hasOption(CONTEXT_OPT)) {
            String arg = cmd.getOptionValue(CONTEXT_OPT);
            String[] args = arg.split(CONTEXT_SEPARATOR);
            if (args.length != REQUIRED_NUMBER_OF_ARGS) {
                throw new SlothException("Invalid argument for '" + CONTEXT_OPT_L + "' parameter");
            }
            sb.setRemoteContext(args[0], args[1], args[2]);
        } else if (context != null) {
            sb.setContext(context);
        }
    }

    protected void printError(Throwable ex) {
        Throwable e = ex;
        while (e != null) {
            String message = e.getMessage();
            if (message != null && !message.isEmpty()) {
                cli.println("ERROR: " + message);
            }
            e = e.getCause();
        }
        if (verbose) {
            cli.printStackTrace(ex);
        }
    }

}
