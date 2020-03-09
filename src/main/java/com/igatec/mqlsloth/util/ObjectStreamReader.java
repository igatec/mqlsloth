package com.igatec.mqlsloth.util;

import com.igatec.mqlsloth.kernel.SlothException;

public interface ObjectStreamReader<C> {

    C next() throws SlothException;
    boolean hasNext();

}
