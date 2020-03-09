package com.igatec.mqlsloth.iface.kernel;

import com.igatec.mqlsloth.kernel.SlothException;

public interface SessionBuilder {

    Session build() throws SlothException;

}
