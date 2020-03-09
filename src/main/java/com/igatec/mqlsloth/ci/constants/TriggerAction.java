package com.igatec.mqlsloth.ci.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public enum TriggerAction {

    APPROVE,
    DEMOTE,
    DISABLE,
    ENABLE,
    IGNORE,
    OVERRIDE,
    PROMOTE,
    REJECT,
    SCHEDULE,
    UNSIGN;

    private static Set<String> stringValues = Collections.unmodifiableSet(Arrays.stream(values()).map(TriggerAction::toString).collect(Collectors.toSet()));

    public static TriggerAction get(String value){
        return valueOf(value.toUpperCase());
    }

    public static Set<String> stringValues(){
        return stringValues;
    }

    @Override
    public String toString(){
        return super.toString().toLowerCase();
    }

}
