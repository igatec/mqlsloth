package com.igatec.mqlsloth.kernel.dbconnector;

import com.igatec.mqlsloth.kernel.SlothException;

public class JPOCompileException extends SlothException {

    public JPOCompileException() {
        super();
    }

    public JPOCompileException(String message) {
        super(message);
    }

    public JPOCompileException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public JPOCompileException(Throwable throwable) {
        super(throwable);
    }

}
