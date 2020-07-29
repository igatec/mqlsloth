package com.igatec.mqlsloth.cli;

import com.igatec.mqlsloth.framework.Context;
import com.igatec.mqlsloth.iface.kernel.ExecutionState;
import com.igatec.mqlsloth.iface.kernel.RealtimeExecutionController;
import com.igatec.mqlsloth.iface.kernel.SearchLocation;
import com.igatec.mqlsloth.iface.kernel.Session;
import com.igatec.mqlsloth.kernel.SlothException;
import com.igatec.mqlsloth.kernel.session.DiffSessionBuilder;
import com.igatec.mqlsloth.kernel.session.SlothApp;
import org.apache.commons.cli.CommandLine;

public class DiffExecutor extends AbstractExecutor {

    public static final int SLEEP_TIME = 200;
    private static final String DIFF_ARGS_ERROR = "Diff task must have argument 's' or 't' defining which "
            + "location (source or target) would be search condition applied to";
    private static final String DIFF_ARGS_T = "t";
    private static final String DIFF_ARGS_S = "s";

    public DiffExecutor(SlothAppCLI cli) {
        super(cli);
    }

    @Override
    public void run(Context context, CommandLine cmd) throws SlothException {

        String[] pattern = cmd.getOptionValues(PATTERN_OPT);
        DiffSessionBuilder builder = SlothApp.getDiffSessionBuilder();
        boolean isImport = cmd.hasOption(IMPORT_OPT);
        builder.setIsImport(isImport);
        if (cmd.hasOption(TARGET_OPT)) {
            if (isImport) {
                throw new SlothException("Import task cannot have 'target' option");
            }
            builder.setFileSystemAsTarget(cmd.getOptionValue(TARGET_OPT));
        } else {
            builder.setDatabaseAsTarget();
        }
        if (cmd.hasOption(SOURCE_OPT) || cmd.hasOption(LOCATION_OPT)) {
            String dir = cmd.getOptionValue(SOURCE_OPT);
            if (dir == null) {
                dir = cmd.getOptionValue(LOCATION_OPT);
            }
            builder.setFileSystemAsSource(dir);
        } else {
            if (isImport) {
                throw new SlothException("Import task must have 'source' option");
            }
            builder.setDatabaseAsSource();
        }
        if (cmd.hasOption(OUTPUT_OPT)) {
            builder.setOutputLocation(cmd.getOptionValue(OUTPUT_OPT));
        } else {
            if (!isImport) {
                throw new SlothException("Diff task must have 'output' option");
            }
        }
        if (isImport) {
            builder.setSearchLocation(SearchLocation.SOURCE);
        } else {
            String loc = cmd.getOptionValue(DIFF_OPT);
            if (loc == null || (!loc.equals(DIFF_ARGS_T) && (!loc.equals(DIFF_ARGS_S)))) {
                throw new SlothException(DIFF_ARGS_ERROR);
            }
            if (loc.equals(DIFF_ARGS_T)) {
                builder.setSearchLocation(SearchLocation.TARGET);
            } else {
                builder.setSearchLocation(SearchLocation.SOURCE);
            }
        }
        builder.setSearchPattern(pattern);
        provideContext(context, cmd, builder);
        if (cmd.hasOption(VERBOSE_OPT)) {
            verbose = true;
        }
        if (cmd.hasOption(SYNC_OPT)) {
            builder.setSyncronous(true);
            sync = true;
        } else {
            sync = false;
        }

        try {
            Session session = builder.build();
            RealtimeExecutionController controller = session.getExecutionController();
            session.run();

            ExecutionState state;
            while ((state = controller.getExecutionState()) != ExecutionState.FINISHED) {
                if (!SlothAppCLI.isMql()) {
                    printStatus(controller.getExecutedAmount(), controller.getExecutionTime(), state);
                } else {
                    cli.print(".");
                }
                Thread.sleep(SLEEP_TIME);
                if (!SlothAppCLI.isMql()) {
                    cli.print("\r"); // This does not work in MQL
                }
            }
            if (SlothAppCLI.isMql()) {
                cli.println("");
            }
            printStatus(controller.getExecutedAmount(), controller.getExecutionTime(), state);
            cli.println("");
            if (controller.isError()) {
                throw controller.getError();
            } else {
                cli.println("Task completed successfully");
            }
        } catch (Throwable ex) {
            cli.println("ERROR: Execution failed");
            printError(ex);
        }
    }

    private void printStatus(int amount, long time, ExecutionState state) {
        cli.print(
                String.format(
                        "Processed %s items. Execution time, ms: %s. %s",
                        amount, time, (state != ExecutionState.FINISHED ? (state + "...") : "")
                )
        );
    }
}
