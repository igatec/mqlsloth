package com.igatec.mqlsloth.iface.kernel;

public interface RealtimeExecutionController {

    ExecutionState getExecutionState();
    boolean isError();
    Throwable getError();
    int getExecutedAmount();
    String getLatestExecutedTask();
    long getExecutionTime();
    String nextMessage(); // This is non-blocking method that returns next session message if exists or null

}
