package com.igatec.mqlsloth.script.assertion;

public class NoBusinessDataOfType extends Assertion {

    String typeName;

    public NoBusinessDataOfType(String typeName){
        this.typeName = typeName;
    }

    @Override
    public String getDescription() {
        return "This is a sample assertion. It checks that there is no business data of type '"+typeName+"' existing.";
    }
}
