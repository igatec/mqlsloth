package com.igatec.mqlsloth.kernel;

public class CommandExecutionException extends SlothException {

    public CommandExecutionException(){
        super();
    }
    public CommandExecutionException(String message){
        super(message);
    }
    public CommandExecutionException(String message, Throwable throwable){
        super(message, throwable);
    }
    public CommandExecutionException(Throwable throwable){
        super(throwable);
    }

}
