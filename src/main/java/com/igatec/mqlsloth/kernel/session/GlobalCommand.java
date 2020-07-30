package com.igatec.mqlsloth.kernel.session;

import com.igatec.mqlsloth.context.ApplicationContext;
import com.igatec.mqlsloth.framework.Context;
import com.igatec.mqlsloth.framework.ContextUtil;
import com.igatec.mqlsloth.framework.MQLCommand;
import com.igatec.mqlsloth.iface.kernel.IMqlCommand;
import com.igatec.mqlsloth.kernel.CommandExecutionException;
import com.igatec.mqlsloth.kernel.SlothException;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class GlobalCommand implements IMqlCommand {
    private static final boolean SYNCHRONOUS = true;

    private final Map<Integer, String> templates = new HashMap<>();
    private static final int TIMEOUT = 3 * 60 * 1000;
    private final Context context;
    private final TransactionMode transactionMode;
    private final MQLCommand command;
    private volatile boolean transactionStarted = false;
    private boolean abortable = true;
    private final ExecutorService executorService;

    public GlobalCommand(Context context, TransactionMode transactionMode) {
        this.context = context;
        this.transactionMode = transactionMode;
        command = ApplicationContext.instance().getMqlCommand();
        if (SYNCHRONOUS) {
            executorService = null;
        } else {
            executorService = Executors.newSingleThreadExecutor(r -> {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            });
        }
    }

    @Override
    public String execute(String... args) throws CommandExecutionException {
        return execute(false, args);
    }

    @Override
    public String execute(boolean allowEmptyItems, String... args) throws CommandExecutionException {
        String[] cleanArgs = allowEmptyItems ? args : clean(args);
        return execute(getCmdTemplate(cleanArgs.length), cleanArgs);
    }

    private synchronized String execute(String cmd, String... args) throws CommandExecutionException {
        try {
            startTransactionIfNeeded();
            return rootExecute(cmd, args);
        } catch (Exception e) {
            abort();
            throw new CommandExecutionException(e);
        }
    }

    private String rootExecute(String cmd, String... args) throws CommandExecutionException {
        String cmdWithQuotes = String.join(" ", args);
        unpackQuotes(args);
        String result;
        List<String> argsList = Arrays.asList(args);

        if (SYNCHRONOUS) {
            try {
//                MQLCommand com = MQLCommand.instance();
                result = command.executeOrThrow(context, cmd, argsList);
            } catch (Exception e) {
                abort();
                throw new CommandExecutionException(e);
            } finally {
                System.out.println("");
            }
        } else {
            try {
                Future<String> future = executorService.submit(() -> command.executeOrThrow(context, cmd, argsList));
                try {
                    result = future.get(TIMEOUT, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    throw new CommandExecutionException(e);
                } catch (ExecutionException e) {
                    throw new CommandExecutionException(e.getCause());
                } catch (TimeoutException e) {
                    /* In case of MQL lag, we don't know if command is or will be executed
                        Workaround is not to close (abort or commit) current transaction despite it causes resources leak
                     */
                    abortable = false;
                    throw new CommandExecutionException("MQL command timeout " + TIMEOUT
                            + " ms exceeded on command: \"" + cmdWithQuotes + "\"", e);
                }
            } catch (CommandExecutionException e) {
                abort();
                throw e;
            }
        }

        return result;
    }

    private void startTransactionIfNeeded() {
        if (transactionMode != TransactionMode.NONE && !transactionStarted) {
            ContextUtil.startTransaction(context, transactionMode == TransactionMode.WRITE);
            transactionStarted = true;
        }
    }

    @Override
    public synchronized void abort() {
        if (transactionStarted && abortable) {
            transactionStarted = false;
            ContextUtil.abortTransaction(context);
        }
    }

    @Override
    public synchronized void commit() throws SlothException {
        if (transactionMode != TransactionMode.NONE && transactionStarted) {
            try {
                transactionStarted = false;
                ContextUtil.commitTransaction(context);
            } catch (Exception e) {
                throw new SlothException(e);
            }
        }
    }

    @Override
    public void close() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    @Override
    public Context getContext() {
        return context;
    }

    private void unpackQuotes(String[] arr) {
        for (int i = 0; i < arr.length; i++) {
            String s = arr[i];
            if (s.startsWith("'") && s.endsWith("'")) {
                arr[i] = s.substring(1, s.length() - 1);
            }
        }
    }

    private String getCmdTemplate(int count) {
        if (!templates.containsKey(count)) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i <= count; i++) {
                if (i != 1) {
                    sb.append(" ");
                }
                sb.append("$");
                sb.append(i);
            }
            templates.put(count, sb.toString());
        }
        return templates.get(count);
    }

    private String[] clean(String[] arr) {
        List<String> list = new LinkedList<>();
        for (String s : arr) {
            if (s == null) {
                list.add(StringUtils.EMPTY);
            } else if (!"".equals(s)) {
                list.add(s);
            }
        }
        return list.toArray(new String[0]);
    }
}
