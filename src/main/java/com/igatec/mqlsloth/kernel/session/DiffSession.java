package com.igatec.mqlsloth.kernel.session;

import com.igatec.mqlsloth.ci.AbstractCI;
import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.ci.util.CIFullName;
import com.igatec.mqlsloth.framework.Context;
import com.igatec.mqlsloth.iface.io.InputProvider;
import com.igatec.mqlsloth.iface.io.OutputProvider;
import com.igatec.mqlsloth.iface.kernel.DiffSessionConfig;
import com.igatec.mqlsloth.iface.kernel.ExecutionState;
import com.igatec.mqlsloth.iface.kernel.PersistenceLocation;
import com.igatec.mqlsloth.iface.kernel.RealtimeExecutionController;
import com.igatec.mqlsloth.iface.kernel.SearchLocation;
import com.igatec.mqlsloth.iface.util.Readable;
import com.igatec.mqlsloth.io.db.DBInputProvider;
import com.igatec.mqlsloth.io.fs.FSComposedOutputProvider;
import com.igatec.mqlsloth.io.fs.FileSystemInputProvider;
import com.igatec.mqlsloth.kernel.SlothException;
import com.igatec.mqlsloth.kernel.config.FileSystemPersistenceLocation;
import com.igatec.mqlsloth.kernel.dbconnector.DBConnector;
import com.igatec.mqlsloth.script.RichMqlScript;
import com.igatec.mqlsloth.script.text.TextScriptBuilder;
import com.igatec.mqlsloth.util.ObjectStreamReader;
import com.igatec.mqlsloth.util.Workspace;

import java.util.Date;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class DiffSession extends AbstractSession {

    protected final OutputProvider oOP;
    protected final Queue<AbstractCI> syncQueue = new LinkedBlockingQueue<>();
    protected final boolean shouldBuildScript;
    protected final boolean shouldSaveScript;
    protected final boolean shouldExecuteScript;
    protected final boolean shouldSaveDiff;
    private final Controller controller = new Controller();
    private final boolean synchronous;

    // todo remove checkstyle ignore and fix
    // CHECKSTYLE.OFF: ParameterNumber
    protected DiffSession(
            Context context,
            PersistenceLocation sourceLocation,
            PersistenceLocation targetLocation,
            FileSystemPersistenceLocation outputLocation,
            SearchLocation searchLocation,
            String[] searchPattern,
            DiffSessionConfig sessionConfig,
            boolean synchronous
    ) throws SlothException {
        super(context, sourceLocation, targetLocation, searchLocation, searchPattern);
        this.synchronous = synchronous;
        oOP = (outputLocation != null) ? new FSComposedOutputProvider(outputLocation.getRootDirectory()) : null;
        shouldSaveDiff = sessionConfig.shouldSaveDiff();
        shouldExecuteScript = sessionConfig.shouldExecuteUpdateScript();
        shouldSaveScript = sessionConfig.shouldSaveUpdateScript();
        shouldBuildScript = shouldExecuteScript || shouldSaveScript;
    }
    // CHECKSTYLE.ON: ParameterNumber

    @Override
    public void run() throws SlothException {
        try {
            InputProvider sIP = sourceLocation.isDatabase()
                    ? new DBInputProvider(this)
                    : new FileSystemInputProvider(sourceLocation.getRootDirectory());
            InputProvider tIP = targetLocation.isDatabase()
                    ? new DBInputProvider(this)
                    : new FileSystemInputProvider(targetLocation.getRootDirectory());

            InputProvider masterProvider;
            InputProvider slaveProvider;
            if (searchLocation == SearchLocation.SOURCE) {
                masterProvider = sIP;
                slaveProvider = tIP;
            } else {
                masterProvider = tIP;
                slaveProvider = sIP;
            }

            ObjectStreamReader<AbstractCI> mReader = null;
            if (getFullNamesPatterns() != null) {
                mReader = masterProvider.getCIDefinitionsByPatterns(getFullNamesPatterns());
            } else {
                throw new SlothException("Search conditions are not defined or not correct");
            }
            final ObjectStreamReader<AbstractCI> masterReader = mReader;

            ObjectStreamReader<CIFullName> nameReader = new ObjectStreamReader<CIFullName>() {
                @Override
                public CIFullName next() throws SlothException {
                    AbstractCI nextCI = masterReader.next();
                    syncQueue.add(nextCI);
                    return nextCI.getCIFullName();
                }

                @Override
                public boolean hasNext() {
                    return masterReader.hasNext();
                }
            };

            ObjectStreamReader<AbstractCI> slaveCIReader = slaveProvider.getCIDefinitions(nameReader);
            RichMqlScript richScript = new RichMqlScript();
            if (oOP != null) {
                oOP.clearAll();
            }
            if (synchronous) {
                executeRun(slaveCIReader, richScript);
            } else {
                ExecutorService service = Executors.newSingleThreadExecutor();
                service.submit(() -> executeRun(slaveCIReader, richScript));
                service.shutdown();
            }
        } catch (Throwable ex) {
            abort();
            throw new SlothException(ex);
        } finally {
            SlothApp.unregisterSession();
            Workspace.deleteAll(this);
        }
    }

    private void executeRun(
            ObjectStreamReader<AbstractCI> slaveCIReader,
            RichMqlScript richScript
    ) {
        try {
            while (slaveCIReader.hasNext()) {
                AbstractCI slaveCI = slaveCIReader.next(); // Can be null
                AbstractCI masterCI = syncQueue.remove();
                AbstractCI sourceCI;
                AbstractCI targetCI;
                if (searchLocation == SearchLocation.SOURCE) {
                    sourceCI = masterCI;
                    targetCI = slaveCI;
                } else {
                    sourceCI = slaveCI;
                    targetCI = masterCI;
                }
                if (targetCI == null) {
                    if (sourceCI == null) {
                        // Do nothing. But this should not happen
                        System.out.println("This shouldn't happen. Check this line please!");
                    } else if (sourceCI.getDiffMode() == CIDiffMode.TARGET) {
                        if (shouldBuildScript) {
                            richScript.addChunks(sourceCI.buildCreateScript());
                        }
                        if (shouldSaveDiff && oOP != null) {
                            oOP.saveCIDefinition(sourceCI);
                        }
                    } else if (sourceCI.getDiffMode() == CIDiffMode.DIFF) {
                        System.out.println("Not implemented yet");
                    } else if (sourceCI.getDiffMode() == CIDiffMode.DELETE) {
                        System.out.println("Not implemented yet");
                    } else {
                        throw new SlothException("Invalid diff mode: " + sourceCI.getDiffMode()); // This should not happen
                    }
                } else if (targetCI.getDiffMode() == CIDiffMode.TARGET) {
                    if (sourceCI == null) {
                        controller.addMessage("WARNING: CI '%s' skipped. Reason: it is not found in SOURCE location", targetCI);
                    } else if (
                            sourceCI.getDiffMode() == CIDiffMode.TARGET
                                    | sourceCI.getDiffMode() == CIDiffMode.DIFF
                    ) {
                        AbstractCI diffCI = targetCI.buildDiff(sourceCI);
                        if (shouldBuildScript) {
                            richScript.addChunks(diffCI.buildUpdateScript());
                        }
                        if (shouldSaveDiff && !diffCI.isEmpty() && oOP != null) {
                            oOP.saveCIDefinition(diffCI);
                        }
                    } else if (sourceCI.getDiffMode() == CIDiffMode.DELETE) {
                        System.out.println("Not implemented yet");
                    } else {
                        throw new SlothException("Invalid diff mode: " + sourceCI.getDiffMode()); // This should not happen
                    }
                } else if (targetCI.getDiffMode() == CIDiffMode.DIFF) {
                    controller.addMessage(
                            "WARNING: CI '%s' skipped. Reason: it is defined in DIFF mode in TARGET location",
                            targetCI
                    );
                } else if (targetCI.getDiffMode() == CIDiffMode.DELETE) {
                    controller.addMessage(
                            "WARNING: CI '%s' skipped. Reason: it is defined in DELETE mode in TARGET location",
                            targetCI
                    );
                } else {
                    throw new SlothException("Invalid diff mode: " + targetCI.getDiffMode()); // This should not happen
                }
                controller.amount++;
            }
            if (shouldSaveScript && oOP != null) {
                Readable sBuilder = new TextScriptBuilder(richScript.iterator());
                oOP.saveUpdateScript(sBuilder);
            }
            if (shouldExecuteScript) {
                controller.setExecutingScript();
                DBConnector writer = new DBConnector(richScript.iterator(), this);
                try {
                    writer.execute();
                } catch (SlothException ex) {
                    abort();
                    throw ex;
                }
            }
            commit();
        } catch (Exception ex) {
            controller.setError(ex);
        } finally {
            controller.setFinished();
        }
    }

    @Override
    public RealtimeExecutionController getExecutionController() {
        return controller;
    }

    public static class Controller implements RealtimeExecutionController {

        ExecutionState state = ExecutionState.BUILDING_DIFF;
        boolean isError = false;
        Throwable ex = null;
        volatile int amount = 0;
        String latestTask = null;
        long startTime = new Date().getTime();
        long endTime = 0;
        BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();

        @Override
        public String nextMessage() {
            return messageQueue.poll();
        }

        private void addMessage(String message) {
            messageQueue.offer(message);
        }

        private void addMessage(String message, Object... params) {
            addMessage(String.format(message, params));
        }

        @Override
        public synchronized ExecutionState getExecutionState() {
            return state;
        }

        private synchronized void setExecutingScript() {
            state = ExecutionState.EXECUTING_SCRIPT;
        }

        private synchronized void setFinished() {
            endTime = new Date().getTime();
            state = ExecutionState.FINISHED;
        }

        @Override
        public synchronized boolean isError() {
            return isError;
        }

        @Override
        public synchronized Throwable getError() {
            return ex;
        }

        private synchronized void setError(Throwable ex) {
            isError = true;
            this.ex = ex;
        }

        @Override
        public int getExecutedAmount() {
            return amount;
        }

        @Override
        public String getLatestExecutedTask() {
            return latestTask;
        }

        @Override
        public long getExecutionTime() {
            if (state != ExecutionState.FINISHED) {
                return new Date().getTime() - startTime;
            } else {
                return endTime - startTime;
            }
        }
    }
}
