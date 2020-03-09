package com.igatec.mqlsloth.cli;

import com.igatec.mqlsloth.ci.AbstractCI;
import com.igatec.mqlsloth.ci.AdminObjectCI;
import com.igatec.mqlsloth.ci.AttributeCI;
import com.igatec.mqlsloth.ci.ChannelCI;
import com.igatec.mqlsloth.ci.CommandCI;
import com.igatec.mqlsloth.ci.ExpressionCI;
import com.igatec.mqlsloth.ci.FormCI;
import com.igatec.mqlsloth.ci.GroupCI;
import com.igatec.mqlsloth.ci.InterfaceCI;
import com.igatec.mqlsloth.ci.MenuCI;
import com.igatec.mqlsloth.ci.NumberGeneratorCI;
import com.igatec.mqlsloth.ci.ObjectGeneratorCI;
import com.igatec.mqlsloth.ci.PolicyCI;
import com.igatec.mqlsloth.ci.PolicyState;
import com.igatec.mqlsloth.ci.PortalCI;
import com.igatec.mqlsloth.ci.ProgramCI;
import com.igatec.mqlsloth.ci.RelationshipCI;
import com.igatec.mqlsloth.ci.RoleCI;
import com.igatec.mqlsloth.ci.StateRecordKey;
import com.igatec.mqlsloth.ci.StateRecordValue;
import com.igatec.mqlsloth.ci.TableCI;
import com.igatec.mqlsloth.ci.TriggerCI;
import com.igatec.mqlsloth.ci.TypeCI;
import com.igatec.mqlsloth.ci.constants.AttributeType;
import com.igatec.mqlsloth.ci.constants.SlothAdminType;
import com.igatec.mqlsloth.framework.Context;
import com.igatec.mqlsloth.iface.io.OutputProvider;
import com.igatec.mqlsloth.io.fs.FileSystemOutputProvider;
import com.igatec.mqlsloth.kernel.SlothException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.LinkedList;

;

public class CreateNewExecutor extends AbstractExecutor {
    public CreateNewExecutor() {
        super(null);
    }

    @Override
    public void run(Context context, CommandLine cmd) throws SlothException {

        String dir = cmd.getOptionValue(LOCATION_OPT);
        if (dir == null)
            throw new SlothException("ERROR: 'Create new' tasks must have '" + LOCATION_OPT + "' option");

        String[] params = cmd.getOptionValues(NEW_OPT);
        if (params == null || params.length < 1)
            throw new SlothException("ERROR: CI type was not specified");
        String ciType = params[0];
        if (params.length < 2)
            throw new SlothException("ERROR: CI name was not specified");
        String ciName = params[1];
        AbstractCI ci;
        SlothAdminType aType = SlothAdminType.getByAlias(ciType);
        if (aType == null)
            throw new SlothException(String.format("ERROR: CI admin type '%s' is invalid", ciType));

        if (SlothAdminType.TYPE.equals(aType)) {
            ci = new TypeCI(ciName);
        } else if (SlothAdminType.ATTRIBUTE.equals(aType)) {
            if (params.length < 3)
                throw new SlothException("ERROR: Attribute type was not specified");
            String attrType = params[2];
            ci = new AttributeCI(ciName, AttributeType.valueOf(attrType.toUpperCase()));
        } else if (SlothAdminType.INTERFACE.equals(aType)) {
            ci = new InterfaceCI(ciName);
        } else if (SlothAdminType.RELATIONSHIP.equals(aType)) {
            ci = new RelationshipCI(ciName);
        } else if (SlothAdminType.POLICY.equals(aType)) {
            ci = new PolicyCI(ciName);
            PolicyState state = ((PolicyCI) ci).addState("default_state");
            state.addAccessRecord(new StateRecordKey(new LinkedList<>(), "default_user", "defaultKey"),
                    new StateRecordValue(Arrays.asList(new String[]{"read", "show"}), new LinkedList<>(), "owner==default_user"));
        } else if (SlothAdminType.PAGE.equals(aType)) {
            ci = new RelationshipCI(ciName);
        } else if (SlothAdminType.PROGRAM.equals(aType)) {
            ci = new ProgramCI(ciName);
            ((ProgramCI) ci).setProgramType(ProgramCI.Type.JAVA);
            String defaultCode = StringUtils.join(new String[]{
                    "import com.igatec.mqlsloth.framework.Context;;", "",
                    "public class " + ciName + "_mxJPO {", "",
                    "    /* Due to 'mxMain' name, this method can be entry point for MQL command line",
                    "    public void mxMain (Context context, String... args) throws Exception {",
                    "    }",
                    "    */",
                    "", "}", ""
            }, System.lineSeparator());
            ((ProgramCI) ci).setCode(defaultCode);
        } else if (SlothAdminType.NUMBER_GENERATOR.equals(aType)) {
            String revision = "";
            if (params.length > 2) revision = params[2];
            ci = new NumberGeneratorCI(ciName, revision);
        } else if (SlothAdminType.OBJECT_GENERATOR.equals(aType)) {
            String revision = "";
            if (params.length > 2) revision = params[2];
            ci = new ObjectGeneratorCI(ciName, revision);
        } else if (SlothAdminType.TRIGGER.equals(aType)) {
            String revision = "";
            if (params.length > 2) revision = params[2];
            ci = new TriggerCI(ciName, revision);
        } else if (SlothAdminType.ROLE.equals(aType)) {
            ci = new RoleCI(ciName);
        } else if (SlothAdminType.GROUP.equals(aType)) {
            ci = new GroupCI(ciName);
        } else if (SlothAdminType.COMMAND.equals(aType)) {
            ci = new CommandCI(ciName);
        } else if (SlothAdminType.MENU.equals(aType)) {
            ci = new MenuCI(ciName);
        } else if (SlothAdminType.PORTAL.equals(aType)) {
            ci = new PortalCI(ciName);
        } else if (SlothAdminType.CHANNEL.equals(aType)) {
            ci = new ChannelCI(ciName);
        } else if (SlothAdminType.EXPRESSION.equals(aType)) {
            ci = new ExpressionCI(ciName);
        } else if (SlothAdminType.FORM.equals(aType)) {
            ci = new FormCI(ciName);
        } else if (SlothAdminType.WEB_TABLE.equals(aType)) {
            ci = new TableCI(ciName);
        } else {
            throw new SlothException(String.format("ERROR: CI file of type '%s' cannot be created with this command", ciType));
        }

        if (ci instanceof AdminObjectCI)
            ((AdminObjectCI) ci).setDefaultSymbolicName();

        OutputProvider oP = new FileSystemOutputProvider(dir);
        oP.saveCIDefinition(ci);
    }
}
