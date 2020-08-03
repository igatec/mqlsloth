package com.igatec.mqlsloth.iface.kernel;

import com.igatec.mqlsloth.kernel.CommandExecutionException;
import com.igatec.mqlsloth.kernel.SlothException;

public interface IMqlCommand {
    String execute(String... args) throws CommandExecutionException;

    String execute(boolean allowEmptyItems, String... args) throws CommandExecutionException;

    void abort();

    void commit() throws SlothException;

    void close();
}
