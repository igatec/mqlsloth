package com.igatec.mqlsloth.kernel.session;

import com.igatec.mqlsloth.framework.Context;
import com.igatec.mqlsloth.iface.kernel.RealtimeExecutionController;
import com.igatec.mqlsloth.iface.kernel.Session;
import com.igatec.mqlsloth.kernel.SlothException;

;

public class SessionWithContext implements Session {

    protected GlobalCommand command;
    private Context context;

    protected SessionWithContext(){}

    public SessionWithContext(Context context, TransactionMode transactionMode){
        command = new GlobalCommand(context, transactionMode);
        this.context = context;
    }

    public SessionWithContext(Context context){
        this(context, TransactionMode.WRITE);
    }

    public String executeSingleMqlCommand(String... args) throws SlothException {
        try {
            String result = command.execute(args);
            commit();
            return result;
        } catch (SlothException ex){
            abort();
            throw ex;
        }
    }

    protected void commit() throws SlothException {
        if (hasCommand()) {
            command.commit();
            command.close();
        }
    }

    protected void abort() {
        if (hasCommand()) {
            command.abort();
            command.close();
        }
    }

    @Override
    public boolean hasCommand(){
        return command != null;
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public void run() throws SlothException { // STUB
    }

    @Override
    public RealtimeExecutionController getExecutionController() { // STUB
        return null;
    }

    @Override
    public GlobalCommand getCommand() throws SlothException {
        if (command == null)
            throw new SlothException("MQL context is not connected");
        return command;
    }
}
