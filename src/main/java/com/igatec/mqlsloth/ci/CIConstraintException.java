package com.igatec.mqlsloth.ci;

public class CIConstraintException extends CIException {

    public CIConstraintException(){
        super();
    }
    public CIConstraintException(String message){
        super(message);
    }
    public CIConstraintException(String message, Throwable throwable){
        super(message, throwable);
    }
    public CIConstraintException(Throwable throwable){
        super(throwable);
    }
}
