package com.igatec.mqlsloth.util;

public class ReversibleSetException extends RuntimeException {

    public ReversibleSetException(){
        super();
    }
    public ReversibleSetException(String message){
        super(message);
    }
    public ReversibleSetException(String message, Throwable throwable){
        super(message, throwable);
    }
    public ReversibleSetException(Throwable throwable){
        super(throwable);
    }

}
