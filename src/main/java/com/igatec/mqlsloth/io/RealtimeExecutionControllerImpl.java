package com.igatec.mqlsloth.io;

import com.igatec.mqlsloth.iface.kernel.ExecutionState;
import com.igatec.mqlsloth.iface.kernel.RealtimeExecutionController;

import java.util.Date;

public class RealtimeExecutionControllerImpl implements RealtimeExecutionController {

    ExecutionState state = ExecutionState.BUILDING_DIFF;
    boolean isError = false;
    Throwable ex = null;
    int amount = 0;
    String latestTask = null;
    long startTime = new Date().getTime();
    long endTime = 0;

    @Override
    public ExecutionState getExecutionState() {
        return state;
    }

    @Override
    public synchronized boolean isError() {
        return isError;
    }

    @Override
    public synchronized Throwable getError() {
        return ex;
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
        if (state == ExecutionState.BUILDING_DIFF){
            return new Date().getTime() - startTime;
        } else {
            return endTime - startTime;
        }
    }

    @Override
    public String nextMessage() {
        return null;
    }
}
