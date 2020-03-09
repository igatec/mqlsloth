package com.igatec.mqlsloth.kernel.session;

import com.igatec.mqlsloth.ci.AbstractCI;
import com.igatec.mqlsloth.ci.util.CIFullName;
import com.igatec.mqlsloth.framework.Context;
import com.igatec.mqlsloth.iface.io.InputProvider;
import com.igatec.mqlsloth.iface.kernel.ExecutionState;
import com.igatec.mqlsloth.iface.kernel.PersistenceLocation;
import com.igatec.mqlsloth.iface.kernel.RealtimeExecutionController;
import com.igatec.mqlsloth.iface.kernel.SearchLocation;
import com.igatec.mqlsloth.io.db.DBInputProvider;
import com.igatec.mqlsloth.io.fs.FileSystemInputProvider;
import com.igatec.mqlsloth.io.fs.FileSystemOutputProvider;
import com.igatec.mqlsloth.kernel.SlothException;
import com.igatec.mqlsloth.util.ObjectStreamReader;
import com.igatec.mqlsloth.util.Workspace;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

;

public class ExportSession extends AbstractSession {

    private FileSystemOutputProvider tOP = new FileSystemOutputProvider(targetLocation.getRootDirectory());
    private final boolean synchronous;

    ExportSession(
            Context context,
            PersistenceLocation sourceLocation,
            PersistenceLocation targetLocation,
            SearchLocation searchLocation,
            String[] searchPattern,
            boolean synchronous
    ) throws SlothException {
        super(context, sourceLocation, targetLocation, searchLocation, searchPattern, TransactionMode.READ);
        this.synchronous = synchronous;
    }

    @Override
    public RealtimeExecutionController getExecutionController() {
        return tOP.getExecutionController();
    }

    @Override
    public void run() throws SlothException {
        try {

            InputProvider sIP = sourceLocation.isDatabase() ? new DBInputProvider(this) : new FileSystemInputProvider(sourceLocation.getRootDirectory());

            ObjectStreamReader<AbstractCI> ciReader = null;

            if (searchLocation == SearchLocation.SOURCE) {
                if (getFullNamesPatterns() != null) {
                    ciReader = sIP.getCIDefinitionsByPatterns(getFullNamesPatterns());
                } else {
                    throw new SlothException("Search conditions are not defined or not correct");
                }
            } else if (searchLocation == SearchLocation.TARGET) {
                InputProvider tIP = new FileSystemInputProvider(targetLocation.getRootDirectory());
                // May be we need firstly to read all names to collection to avoid possible read-write conflict
                ObjectStreamReader<CIFullName> nameReader;
                if (getFullNamesPatterns() != null) {
                    nameReader = tIP.getCINamesByPatterns(getFullNamesPatterns());
                } else {
                    throw new SlothException("Search conditions are not defined or not correct");
                }
                ciReader = sIP.getCIDefinitions(nameReader);
            } // No more options

            RealtimeExecutionController controller = getExecutionController();

            if (synchronous) {
                tOP.saveCIDefinitionsSynchronously(ciReader);
                boolean success = false;
                if (controller.getExecutionState() == ExecutionState.FINISHED && !controller.isError())
                    success = true;
                if (success)
                    commit();
                else
                    abort();
            } else {
                tOP.saveCIDefinitions(ciReader);
                ExecutorService service = Executors.newSingleThreadExecutor();
                service.submit(() -> {
                    try {
                        boolean success = false;
                        while (controller.getExecutionState() != ExecutionState.FINISHED) {
                            Thread.sleep(20);
                        }
                        if (controller.getExecutionState() == ExecutionState.FINISHED && !controller.isError())
                            success = true;
                        if (success)
                            commit();
                        else
                            abort();
                    } catch (SlothException | InterruptedException e) {
                        // This transaction is not writing, so commiting or aborting are just to close transaction
                    }
                });
                service.shutdown();
            }

        } catch (Throwable ex) {
            throw new SlothException(ex);
        } finally {
            SlothApp.unregisterSession();
            Workspace.deleteAll(this);
        }

    }

}
