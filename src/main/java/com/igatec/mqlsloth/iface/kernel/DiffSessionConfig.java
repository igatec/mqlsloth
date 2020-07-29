package com.igatec.mqlsloth.iface.kernel;

public interface DiffSessionConfig {

    boolean shouldSaveUpdateScript();

    boolean shouldSaveDiff();

    boolean shouldExecuteUpdateScript();

    boolean executeInTransaction();

}
