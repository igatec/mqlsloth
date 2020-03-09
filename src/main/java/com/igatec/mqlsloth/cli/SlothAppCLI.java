package com.igatec.mqlsloth.cli;

import com.igatec.mqlsloth.framework.Context;
import com.igatec.mqlsloth.framework.MatrixWriter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.types.Commandline;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

;

public class SlothAppCLI implements CLIConstants {

    private Context context = null;
    private final InteractiveSession interactiveSession = new InteractiveSession();
    private static boolean isMql = false;
    private MatrixWriter matrixWriter = null;

    public static boolean isMql() {
        return isMql;
    }

    public static void main(String[] args) throws Exception {
        main(null, args);
    }

    public static void main(Context context, String[] args) throws Exception {
        new SlothAppCLI().run(context, args);
    }

    private void run(Context context, String[] args) throws Exception {
        if (context != null)
            isMql = true;
        boolean keep = false;
        Scanner clScanner = null;
        if (!isMql)
            clScanner = new Scanner(System.in); // This line kills MQL
        do {
            this.context = context;
            Options options = getOptions();
            try {
                CommandLine cmd = new GnuParser().parse(options, args);
                if (cmd.hasOption(CONTEXT_OPT))
                    interactiveSession.setContextArgValue(cmd.getOptionValue(CONTEXT_OPT));
                if (cmd.hasOption(HELP_OPT)) {
                    HelpFormatter formatter = new HelpFormatter();
                    try (InputStream is = getClass().getResourceAsStream("/sloth_help_message.txt")) {
                        String helpMessage = IOUtils.toString(is);
                        System.out.println(helpMessage);
                    } catch (Throwable th) {
                    }
                    formatter.printHelp("ant", options);
                } else {
                    AbstractExecutor executor = getExecutor(cmd);
                    if (executor != null)
                        executor.run(context, cmd);
                    flushWriter();
                }

                if (cmd.hasOption(KEEP_OPT) && !isMql)
                    keep = !keep;

            } catch (Exception ex) {
                if (keep) {
                    System.out.println(ex.getMessage());
                } else {
                    flushWriter();
                    closeWriter();
                    throw ex;
                }
            }

            if (keep) {
                System.out.println();
                System.out.print(" > ");
                String next = clScanner.nextLine();
                String contextValue = interactiveSession.getContextArgValue();
                if (contextValue != null)
                    next += String.format(" -%s %s", CONTEXT_OPT, contextValue);
                args = Commandline.translateCommandline(next);
            }

        } while (keep);
        if (!isMql)
            clScanner.close();
        flushWriter();
        closeWriter();
    }

    public Context getContext() {
        return context;
    }

    private AbstractExecutor getExecutor(CommandLine cmd) {
        AbstractExecutor executor = null;
        if (cmd.hasOption(EXPORT_OPT))
            executor = returnExecutorOrThrow(executor, new ExportExecutor(this));
        if (cmd.hasOption(IMPORT_OPT) || cmd.hasOption(DIFF_OPT))
            executor = returnExecutorOrThrow(executor, new DiffExecutor(this));
        if (cmd.hasOption(UPDATE_OPT))
            executor = returnExecutorOrThrow(executor, new UpdateExecutor(this));
        if (cmd.hasOption(EXEC_OPT))
            executor = returnExecutorOrThrow(executor, new ExecExecutor(this));
        if (cmd.hasOption(NEW_OPT))
            executor = returnExecutorOrThrow(executor, new CreateNewExecutor());
        return executor;
    }

    private AbstractExecutor returnExecutorOrThrow(AbstractExecutor ex1, AbstractExecutor ex2) {
        if (ex1 != null)
            throw new RuntimeException("Command line error: Two tasks cannot be executed within one command: " + ex1 + ", " + ex2);
        return ex2;
    }

    private Options getOptions() {
        Options opts = new Options();
        opts.addOption(HELP_OPT, HELP_OPT_L, false, HELP_OPT_D);
        opts.addOption(KEEP_OPT, KEEP_OPT_L, false, KEEP_OPT_D);

        Option execOpt = new Option(EXEC_OPT, EXEC_OPT_L, true, EXEC_OPT_D);
        execOpt.setArgs(Option.UNLIMITED_VALUES);
        opts.addOption(execOpt);

        Option newOpt = new Option(NEW_OPT, NEW_OPT_L, true, NEW_OPT_D);
        newOpt.setArgs(Option.UNLIMITED_VALUES);
        opts.addOption(newOpt);

        opts.addOption(EXPORT_OPT, EXPORT_OPT_L, false, EXPORT_OPT_D);
        opts.addOption(IMPORT_OPT, IMPORT_OPT_L, false, IMPORT_OPT_D);
        opts.addOption(UPDATE_OPT, UPDATE_OPT_L, false, UPDATE_OPT_D);
        opts.addOption(DIFF_OPT, DIFF_OPT_L, true, DIFF_OPT_D);

        opts.addOption(SOURCE_OPT, SOURCE_OPT_L, true, SOURCE_OPT_D);
        opts.addOption(TARGET_OPT, TARGET_OPT_L, true, TARGET_OPT_D);
        opts.addOption(LOCATION_OPT, LOCATION_OPT_L, true, LOCATION_OPT_D);
        opts.addOption(OUTPUT_OPT, OUTPUT_OPT_L, true, OUTPUT_OPT_D);

        opts.addOption(CONTEXT_OPT, CONTEXT_OPT_L, true, CONTEXT_OPT_D);
        opts.addOption(SYNC_OPT, SYNC_OPT_L, false, SYNC_OPT_D);

        Option patternOpt = new Option(PATTERN_OPT, PATTERN_OPT_L, true, PATTERN_OPT_D);
        patternOpt.setArgs(Option.UNLIMITED_VALUES);
        opts.addOption(patternOpt);
//        opts.addOption(FINE_OPT, FINE_OPT_L, true, FINE_OPT_D);
        opts.addOption(VERBOSE_OPT, VERBOSE_OPT_L, false, VERBOSE_OPT_D);

        return opts;
    }

    public void print(String text) {
        if (isMql()) {
            initMatrixWriter();
            try {
                matrixWriter.write(text);
            } catch (Exception e) {
            }
        } else {
            System.out.print(text);
        }
    }

    public void flushWriter() throws IOException {
        if (matrixWriter != null) {
            matrixWriter.flush();
        }
    }

    private void closeWriter() throws IOException {
        if (matrixWriter != null) {
            matrixWriter.close();
        }
    }

    public void println(String text) {
        if (isMql()) {
            initMatrixWriter();
            try {
                matrixWriter.write(text);
                matrixWriter.write('\n');
            } catch (Exception e) {
            }
        } else {
            System.out.println(text);
        }
    }

    public void printStackTrace(Throwable th) {
        th.printStackTrace();
    }

    private void initMatrixWriter() {
        if (matrixWriter == null)
            matrixWriter = new MatrixWriter(context);
    }

    private static class InteractiveSession {

        private String contextArgValue;

        public String getContextArgValue() {
            return contextArgValue;
        }

        public void setContextArgValue(String contextArgValue) {
            this.contextArgValue = contextArgValue;
        }
    }

}
