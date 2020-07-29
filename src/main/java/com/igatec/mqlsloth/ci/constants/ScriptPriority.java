package com.igatec.mqlsloth.ci.constants;

// todo remove ignorance and refactor
// CHECKSTYLE.OFF: InterfaceIsType
public interface ScriptPriority {

    int SP_AFTER_ADMIN_CREATION_1 = Integer.MIN_VALUE + 1;
    int SP_AFTER_ADMIN_CREATION_2 = Integer.MIN_VALUE + 2;
    int SP_AFTER_ADMIN_CREATION_3 = Integer.MIN_VALUE + 3;

    int SP_AFTER_ADMIN_CREATION_501 = Integer.MIN_VALUE + 501;
    int SP_AFTER_ADMIN_CREATION_502 = Integer.MIN_VALUE + 502;
    int SP_AFTER_ADMIN_CREATION_503 = Integer.MIN_VALUE + 503;
    int SP_AFTER_ADMIN_CREATION_504 = Integer.MIN_VALUE + 504;
    int SP_AFTER_ADMIN_CREATION_505 = Integer.MIN_VALUE + 505;

    int SP_SET_DERIVED = -10;
    int SP_ADD_ATTRIBUTE_TO = 10;
    int SP_ADD_TYPE_TO = 10;
    int SP_ADD_REL_TO = 10;

    int SP_REM_ATTR_DEFAULT = 1000;
    int SP_SET_ATTR_NOT_MULTIVALUE = 1000;
    int SP_SET_ATTR_MULTIVALUE = 1110;
    int SP_SET_ATTR_DEFAULT = 1120;

    int SP_BUS_CREATION = 2000;
    int SP_BUS_MODIFICATION = 3000;
}
// CHECKSTYLE.ON: InterfaceIsType
