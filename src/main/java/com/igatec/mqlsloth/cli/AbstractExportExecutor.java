package com.igatec.mqlsloth.cli;

import com.igatec.mqlsloth.framework.Context;
import com.igatec.mqlsloth.iface.kernel.ExecutionState;
import com.igatec.mqlsloth.iface.kernel.RealtimeExecutionController;
import com.igatec.mqlsloth.iface.kernel.SearchLocation;
import com.igatec.mqlsloth.iface.kernel.Session;
import com.igatec.mqlsloth.kernel.SlothException;
import com.igatec.mqlsloth.kernel.session.ExportSessionBuilder;
import com.igatec.mqlsloth.kernel.session.SlothApp;
import org.apache.commons.cli.CommandLine;

;

public abstract class AbstractExportExecutor extends AbstractExecutor {

    public AbstractExportExecutor(SlothAppCLI cli) {
        super(cli);
    }

    protected abstract SearchLocation getSearchLocation();

    protected abstract void printStatus(int amount, long time);

    protected abstract void printSuccess();

    protected abstract void printFailure();

    @Override
    public void run(Context context, CommandLine cmd) throws SlothException {

        String dir = cmd.getOptionValue(TARGET_OPT);
        if (dir == null)
            dir = cmd.getOptionValue(LOCATION_OPT);
        if (dir == null)
            throw new SlothException("Export and Update tasks must have '"+TARGET_OPT+"' option");
        String[] pattern = cmd.getOptionValues(PATTERN_OPT);
        ExportSessionBuilder builder = SlothApp.getExportSessionBuilder();
        builder.setFileSystemAsTarget(dir);
        String srcDir = cmd.getOptionValue(SOURCE_OPT);
        if (srcDir == null)
            builder.setDatabaseAsSource();
        else
            builder.setFileSystemAsSource(srcDir);
        builder.setSearchLocation(getSearchLocation());
        builder.setSearchPattern(pattern);
        provideContext(context, cmd, builder);
        if (cmd.hasOption(VERBOSE_OPT))
            verbose = true;
        if (cmd.hasOption(SYNC_OPT)){
            builder.setSyncronous(true);
            sync = true;
        } else {
            sync = false;
        }

        try {
            Session session = builder.build();
            RealtimeExecutionController controller = session.getExecutionController();
            session.run();

            while (controller.getExecutionState() != ExecutionState.FINISHED) {
                if (!SlothAppCLI.isMql())
                    printStatus(controller.getExecutedAmount(), controller.getExecutionTime());
                else
                    cli.print(".");
                Thread.sleep(200);
                if (!SlothAppCLI.isMql())
                    cli.print("\r");
            }
            if (SlothAppCLI.isMql())
                cli.println("");
            printStatus(controller.getExecutedAmount(), controller.getExecutionTime());
            cli.println("");
            if (controller.isError()) {
                throw controller.getError();
            } else {
                printSuccess();
            }

        } catch (Throwable ex){
            printFailure();
            printError(ex);
        }

    }

}
