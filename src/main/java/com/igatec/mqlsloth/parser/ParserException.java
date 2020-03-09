package com.igatec.mqlsloth.parser;

public class ParserException extends Exception {

    public ParserException(){
        super();
    }
    public ParserException(String message){
        super(message);
    }
    public ParserException(String message, Throwable throwable){
        super(message, throwable);
    }
    public ParserException(Throwable throwable){
        super(throwable);
    }

}
