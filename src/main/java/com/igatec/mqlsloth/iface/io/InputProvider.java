package com.igatec.mqlsloth.iface.io;

import com.igatec.mqlsloth.ci.AbstractCI;
import com.igatec.mqlsloth.ci.util.CIFullName;
import com.igatec.mqlsloth.kernel.SlothException;
import com.igatec.mqlsloth.util.ObjectStreamReader;

import java.util.Collection;

public interface InputProvider {
    AbstractCI getCIDefinition(CIFullName fullName) throws SlothException;

    boolean containsCIDefinition(CIFullName fullName);

    ObjectStreamReader<AbstractCI> getAllCIDefinitions();

    ObjectStreamReader<AbstractCI> getCIDefinitions(String name);

    ObjectStreamReader<AbstractCI> getCIDefinitionsByPattern(String namePattern) throws SlothException;

    ObjectStreamReader<AbstractCI> getCIDefinitions(ObjectStreamReader<CIFullName> fullNamesIter);

    ObjectStreamReader<AbstractCI> getCIDefinitionsByPattern(CIFullName fullNamePattern) throws SlothException;

    ObjectStreamReader<AbstractCI> getCIDefinitionsByPatterns(Collection<CIFullName> fullNamesPatterns) throws SlothException;

    ObjectStreamReader<CIFullName> getAllCINames();

    ObjectStreamReader<CIFullName> getCINamesByPattern(String namePattern) throws SlothException;

    ObjectStreamReader<CIFullName> getCINamesByPattern(CIFullName fullNamePattern) throws SlothException;

    ObjectStreamReader<CIFullName> getCINamesByPatterns(Collection<CIFullName> fullNamesPatterns) throws SlothException;
}
