package com.igatec.mqlsloth.io;

import com.igatec.mqlsloth.ci.AbstractCI;
import com.igatec.mqlsloth.iface.io.OutputProvider;
import com.igatec.mqlsloth.iface.kernel.ExecutionState;
import com.igatec.mqlsloth.iface.kernel.RealtimeExecutionController;
import com.igatec.mqlsloth.util.ObjectStreamReader;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractOutputProvider implements OutputProvider {

    private final RealtimeExecutionControllerImpl controller = new RealtimeExecutionControllerImpl();

    public RealtimeExecutionController getExecutionController() {
        return controller;
    }

    @Override
    public void saveCIDefinitions(ObjectStreamReader<AbstractCI> ciIter) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(() -> {
            saveCIDefs(ciIter);
        });
        service.shutdown();
    }

    @Override
    public void saveCIDefinitionsSynchronously(ObjectStreamReader<AbstractCI> ciIter) {
        saveCIDefs(ciIter);
    }

    private void saveCIDefs(ObjectStreamReader<AbstractCI> ciIter) {
        try {
            while (ciIter.hasNext()) {
                AbstractCI ci = ciIter.next();
                saveCIDefinition(ci);
                controller.amount++;
            }
        } catch (Throwable ex) {
            synchronized (controller) {
                controller.ex = ex;
                controller.isError = true;
            }
        } finally {
            controller.state = ExecutionState.FINISHED;
            controller.endTime = new Date().getTime();
        }
    }

}
