package com.igatec.mqlsloth.iface.kernel;

import com.igatec.mqlsloth.framework.Context;
import com.igatec.mqlsloth.kernel.SlothException;

;

public interface Session {

    void run() throws SlothException;
    RealtimeExecutionController getExecutionController();
    IMqlCommand getCommand() throws SlothException;
    boolean hasCommand();
    public Context getContext();

}
