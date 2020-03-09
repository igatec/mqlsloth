package com.igatec.mqlsloth.cli;

import com.igatec.mqlsloth.framework.Context;
import com.igatec.mqlsloth.kernel.SlothException;
import com.igatec.mqlsloth.kernel.session.SessionWithContext;
import com.igatec.mqlsloth.kernel.session.SessionWithContextBuilder;
import org.apache.commons.cli.CommandLine;

;

public class ExecExecutor extends AbstractExecutor {
    public ExecExecutor(SlothAppCLI cli) {
        super(cli);
    }

    @Override
    public void run(Context context, CommandLine cmd) throws SlothException {
        String[] command = cmd.getOptionValues(EXEC_OPT);
        SessionWithContextBuilder sb = new SessionWithContextBuilder();
        provideContext(context, cmd, sb);
        SessionWithContext session = (SessionWithContext) sb.build();
        try {
            String result = session.executeSingleMqlCommand(command);
            System.out.print(result);
        } catch (SlothException ex){
            System.out.print(ex);
        }
    }

    @Override
    public String toString(){
        return SlothAppCLI.EXEC_OPT_L;
    }
}
