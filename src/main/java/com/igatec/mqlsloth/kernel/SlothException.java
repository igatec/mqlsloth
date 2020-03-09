package com.igatec.mqlsloth.kernel;

public class SlothException extends Exception {

    public SlothException(){
        super();
    }
    public SlothException(String message){
        super(message);
    }
    public SlothException(String message, Throwable throwable){
        super(message, throwable);
    }
    public SlothException(Throwable throwable){
        super(throwable);
    }

}
