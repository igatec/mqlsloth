package com.igatec.mqlsloth.kernel.session;

import com.igatec.mqlsloth.ci.NumberGeneratorCI;
import com.igatec.mqlsloth.ci.ObjectGeneratorCI;
import com.igatec.mqlsloth.ci.TriggerCI;
import com.igatec.mqlsloth.ci.constants.SlothAdminType;
import com.igatec.mqlsloth.ci.util.CIFullName;
import com.igatec.mqlsloth.ci.util.StringCIName;
import com.igatec.mqlsloth.framework.Context;
import com.igatec.mqlsloth.iface.kernel.PersistenceLocation;
import com.igatec.mqlsloth.iface.kernel.SearchLocation;
import com.igatec.mqlsloth.kernel.SlothException;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

;

public abstract class AbstractSession extends SessionWithContext {

    protected final PersistenceLocation sourceLocation;
    protected final PersistenceLocation targetLocation;
    protected final SearchLocation searchLocation;
    private Collection<CIFullName> fullNamesPatterns = null;
    private final Context context;

    protected AbstractSession(
            Context context,
            PersistenceLocation sourceLocation,
            PersistenceLocation targetLocation,
            SearchLocation searchLocation,
            String[] searchPattern
    ) throws SlothException {
        this(
                context, sourceLocation, targetLocation,
                searchLocation, searchPattern, TransactionMode.WRITE
        );
    }

    protected AbstractSession(
            Context context,
            PersistenceLocation sourceLocation,
            PersistenceLocation targetLocation,
            SearchLocation searchLocation,
            String[] searchPattern,
            TransactionMode transactionMode
    ) throws SlothException {
        if (targetLocation.isDatabase() && context == null)
            throw new SlothException("Context was not provided for export session");
        if (context == null)
            command = null;
        else
            command = new GlobalCommand(context, transactionMode);
        this.sourceLocation = sourceLocation;
        this.targetLocation = targetLocation;
        this.searchLocation = searchLocation;
        extractPatterns(searchPattern);
        this.context = context;
    }

    @Override
    public Context getContext() {
        return context;
    }

    protected void extractPatterns(String[] args) throws SlothException {
        if (args == null) {
            throw new SlothException("Search condition are not specified");
        }
        if (args.length > 3) {
            throw new SlothException("Invalid number of parameters");
        } else if (args.length == 1) {
            throw new SlothException("Invalid number of parameters");
        } else if (args.length > 1) {
            List<CIFullName> patterns = new LinkedList<>();
            int i = 0;
            SlothAdminType aType = SlothAdminType.getByAlias(args[i]);
            if (aType == null)
                throw new SlothException("Invalid search condition");
            try {
                String name = args[++i];
                String revision = args.length == 3 ? args[++i] : "*";
                if (aType == SlothAdminType.NUMBER_GENERATOR) {
                    patterns.add(NumberGeneratorCI.createCIFullName(name, revision));
                } else if (aType == SlothAdminType.OBJECT_GENERATOR) {
                    patterns.add(ObjectGeneratorCI.createCIFullName(name, revision));
                } else if (aType == SlothAdminType.TRIGGER) {
                    patterns.add(TriggerCI.createCIFullName(name, revision));
                } else if (aType == SlothAdminType.ALL) {
                    patterns.add(NumberGeneratorCI.createCIFullName(name, revision));
                    patterns.add(ObjectGeneratorCI.createCIFullName(name, revision));
                    patterns.add(TriggerCI.createCIFullName(name, revision));
                    patterns.add(new CIFullName(aType, new StringCIName(name)));
                } else {
                    patterns.add(new CIFullName(aType, new StringCIName(name)));
                }
            } catch (IndexOutOfBoundsException ex) {
                throw new SlothException("Invalid search condition");
            }
            fullNamesPatterns = patterns;
        }
    }

    protected Collection<CIFullName> getFullNamesPatterns() {
        return new LinkedList<>(fullNamesPatterns);
    }

}
