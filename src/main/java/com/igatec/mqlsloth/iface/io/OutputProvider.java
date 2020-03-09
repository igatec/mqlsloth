package com.igatec.mqlsloth.iface.io;

import com.igatec.mqlsloth.ci.AbstractCI;
import com.igatec.mqlsloth.iface.util.Readable;
import com.igatec.mqlsloth.kernel.SlothException;
import com.igatec.mqlsloth.util.ObjectStreamReader;

public interface OutputProvider {

    void saveCIDefinition(AbstractCI ci) throws SlothException;

    void saveCIDefinitions(ObjectStreamReader<AbstractCI> ciIter);

    void saveCIDefinitionsSynchronously(ObjectStreamReader<AbstractCI> ciIter);

    void saveUpdateScript(Readable script) throws SlothException;

    boolean clearAll();

}
