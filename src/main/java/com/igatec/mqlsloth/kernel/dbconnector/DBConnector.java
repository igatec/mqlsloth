package com.igatec.mqlsloth.kernel.dbconnector;

import com.igatec.mqlsloth.iface.kernel.IMqlCommand;
import com.igatec.mqlsloth.iface.kernel.Session;
import com.igatec.mqlsloth.kernel.CommandExecutionException;
import com.igatec.mqlsloth.kernel.SlothException;
import com.igatec.mqlsloth.kernel.session.SlothApp;
import com.igatec.mqlsloth.script.*;
import com.igatec.mqlsloth.script.action.JPOCompileAction;
import com.igatec.mqlsloth.script.action.ModSymbolicNameAction;
import com.igatec.mqlsloth.util.Workspace;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.*;

import static com.igatec.mqlsloth.script.MqlKeywords.*;

public class DBConnector {

    private IMqlCommand command = null;
    private final Iterator<MqlAction> iterator;
    private Session currentSession = null;
    private final static String JPO_FILE_SUFFIX = "_mxJPO.java";

    public DBConnector(Iterator<MqlAction> iterator){
        this.iterator = iterator;
    }
    public DBConnector(Iterator<MqlAction> iterator, Session currentSession){
        this(iterator);
        this.currentSession = currentSession;
    }

    public void execute() throws SlothException {
        List<JPOCompileAction> compileActions = new LinkedList<>();
        List<ModSymbolicNameAction> symbolicNameActions = new LinkedList<>();
        if (command == null){
            command = ((currentSession==null) ? SlothApp.getCurrentSession() : currentSession).getCommand();
        }
        while (iterator.hasNext()){
            MqlAction action = iterator.next();
            if (action instanceof MqlCommand) {
                MqlCommand cmd = (MqlCommand) action;
                String[] params = cmd.plainify();
                try {
                    String result = command.execute(params);
                } catch (CommandExecutionException ex) {
                    throw new SlothException("Could not execute command" + System.lineSeparator() +
                            StringUtils.join(params, ' '), ex);
                }
            } else if (action instanceof JPOCompileAction){
                compileActions.add((JPOCompileAction) action);
            } else if (action instanceof ModSymbolicNameAction){
                symbolicNameActions.add((ModSymbolicNameAction) action);
            }
        }
        compile(compileActions);
        modSymbolicNames(symbolicNameActions);
    }

    private void modSymbolicNames(List<ModSymbolicNameAction> actions) throws CommandExecutionException {
        for (ModSymbolicNameAction action: actions){
            String type = action.getCiType();
            String name = action.getCiName();
            String queryResult = "";
            if (type.equals("table")) {
                queryResult = command.execute(M_LIST, M_PROPERTY, M_ON, M_PROGRAM, MqlUtil.SYMBOLIC_NAME_PROGRAM,
                        M_TO, type, name, "system");
            } else {
                queryResult = command.execute(
                        M_LIST, M_PROPERTY, M_ON, M_PROGRAM, MqlUtil.SYMBOLIC_NAME_PROGRAM, M_TO, type, name
                );
            }

            String[] resultLines = MqlUtil.splitToLines(queryResult);
            for (String s: resultLines){
                String propName = s.split(" on program ")[0];
                command.execute(M_MODIFY, M_PROGRAM, MqlUtil.SYMBOLIC_NAME_PROGRAM,
                        M_REMOVE, M_PROPERTY, propName, M_TO, type, name);
            }
            String newSymbolicName = action.getSymbolicName();
            if (newSymbolicName != null && !newSymbolicName.isEmpty()) {
                if (type.equals("table")) {
                    command.execute(M_MODIFY, M_PROGRAM, MqlUtil.SYMBOLIC_NAME_PROGRAM,
                            M_ADD, M_PROPERTY, newSymbolicName, M_TO, type, name, "system");
                } else {
                    command.execute(M_MODIFY, M_PROGRAM, MqlUtil.SYMBOLIC_NAME_PROGRAM,
                            M_ADD, M_PROPERTY, newSymbolicName, M_TO, type, name);
                }
            }
        }
    }

    private void compile(List<JPOCompileAction> actions) throws SlothException {
        Workspace ws = Workspace.create(currentSession);
        File wsFile = ws.getDir();
        String wsPath = wsFile.getName();
        for (JPOCompileAction action: actions){
            try {
                FileUtils.writeStringToFile(
                        new File(wsPath + File.separator + action.getProgName() + JPO_FILE_SUFFIX),
                        action.getCode(), false);
            } catch (Exception ex){
                throw new SlothException(ex);
            }
        }
        if (!actions.isEmpty()) {
            command.execute(MqlKeywords.M_INSERT, MqlKeywords.M_PROGRAM, wsPath + File.separator);
        }
        for (JPOCompileAction action:actions){
            command.execute(MqlKeywords.M_COMPILE, MqlKeywords.M_PROGRAM, action.getProgName());
        }
    }

}
