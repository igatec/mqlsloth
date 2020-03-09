package com.igatec.mqlsloth.script;

public class ScriptBuildingException extends RuntimeException {
    public ScriptBuildingException(){
        super();
    }
    public ScriptBuildingException(String message){
        super(message);
    }
    public ScriptBuildingException(String message, Throwable throwable){
        super(message, throwable);
    }
    public ScriptBuildingException(Throwable throwable){
        super(throwable);
    }

}
