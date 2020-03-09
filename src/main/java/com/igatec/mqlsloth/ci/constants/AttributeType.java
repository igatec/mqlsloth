package com.igatec.mqlsloth.ci.constants;

import com.igatec.mqlsloth.script.MqlKeywords;

public enum AttributeType {

    STRING (MqlKeywords.M_STRING),
    INTEGER (MqlKeywords.M_INTEGER),
    REAL (MqlKeywords.M_REAL),
    TIMESTAMP (MqlKeywords.M_TIMESTAMP),
    ENUM (MqlKeywords.M_ENUM),
    BOOLEAN (MqlKeywords.M_BOOLEAN),
    BINARY (MqlKeywords.M_BINARY);

    private String mqlKeyword;

    AttributeType(String mqlKeyword){
        this.mqlKeyword = mqlKeyword;
    }

    @Override
    public String toString(){
        return mqlKeyword;
    }

    public String getMqlKeyword(){
        return mqlKeyword;
    }

}
