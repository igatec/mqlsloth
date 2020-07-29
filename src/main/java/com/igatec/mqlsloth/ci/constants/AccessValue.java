package com.igatec.mqlsloth.ci.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public enum AccessValue {

    ALL,
    APPROVE,
    CHANGENAME,
    CHANGEOWNER,
    CHANGEPOLICY,
    CHANGESOV,
    CHANGETYPE,
    CHANGEVAULT,
    CHECKIN,
    CHECKOUT,
    CREATE,
    DELETE,
    DEMOTE,
    DISABLE,
    ENABLE,
    EXECUTE,
    FREEZE,
    FROMCONNECT,
    FROMDISCONNECT,
    GRANT,
    IGNORE,
    LOCK,
    MAJORREVISE,
    MODIFY,
    MODIFYFORM,
    NONE,
    OVERRIDE,
    PROMOTE,
    READ,
    REJECT,
    RESERVE,
    REVISE,
    REVOKE,
    SCHEDULE,
    SHOW,
    THAW,
    TOCONNECT,
    TODISCONNECT,
    UNLOCK,
    UNRESERVE,
    VIEWFORM,
    ADDINTERFACE,
    REMOVEINTERFACE;

    private static Set<String> stringValues = Collections.unmodifiableSet(
            Arrays.stream(values())
                    .map(AccessValue::toString)
                    .collect(Collectors.toSet())
    );

    public static AccessValue get(String value) {
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
