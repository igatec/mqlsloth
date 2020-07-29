package com.igatec.mqlsloth.ci.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public enum TriggerPhase {

    CHECK,
    ACTION,
    OVERRIDE;

    private static Set<String> stringValues = Collections.unmodifiableSet(
            Arrays.stream(values())
                    .map(TriggerPhase::toString)
                    .collect(Collectors.toSet())
    );

    public static TriggerPhase get(String value) {
        return valueOf(value.toUpperCase());
    }

    public static Set<String> stringValues() {
        return stringValues;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

}
