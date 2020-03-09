package com.igatec.mqlsloth.ci.constants;

import java.util.*;

import static com.igatec.mqlsloth.script.MqlKeywords.*;

public enum SlothAdminType {
    TYPE(0, M_TYPE, MqlAdminType.TYPE, "type"),
    ATTRIBUTE(1, M_ATTRIBUTE, MqlAdminType.ATTRIBUTE, "attribute", "attr"),
    //    DIMENSION(2, M_DIMENSION, MqlAdminType.DIMENSION, "dimension", "dim"),
    INTERFACE(4, M_INTERFACE, MqlAdminType.INTERFACE, "interface", "iface"),
    POLICY(5, M_POLICY, MqlAdminType.POLICY, "policy"),
    RELATIONSHIP(6, M_RELATIONSHIP, MqlAdminType.RELATIONSHIP, "relationship", "rel"),
    PAGE(10, M_PAGE, MqlAdminType.PAGE, "page"),
    PROGRAM(11, M_PROGRAM, MqlAdminType.PROGRAM, "program", "prog"),
    COMMAND(16, M_COMMAND, MqlAdminType.COMMAND, "command", "cmd"),
    MENU(17, M_MENU, MqlAdminType.MENU, "menu"),
    CHANNEL(18, M_CHANNEL, MqlAdminType.CHANNEL, "channel"),
    PORTAL(19, M_PORTAL, MqlAdminType.PORTAL, "portal"),
    WEB_TABLE(22, M_TABLE, MqlAdminType.TABLE, "table", "tab"),
    BUS(23, M_BUS, MqlAdminType.BUSINESS_OBJECT, "businessobject", "bus"),
    NUMBER_GENERATOR(M_NUMBER_GENERATOR, 25, M_NUMBER_GENERATOR, M_BUS, MqlAdminType.BUSINESS_OBJECT,
            "numbergenerator", "ng"),
    OBJECT_GENERATOR(M_OBJECT_GENERATOR, 26, M_OBJECT_GENERATOR, M_BUS, MqlAdminType.BUSINESS_OBJECT,
            "objectgenerator", "og"),
    TRIGGER(M_TRIGGER_INPUT, 28, M_TRIGGER_INPUT, M_BUS, MqlAdminType.BUSINESS_OBJECT,
            "trigger"),
    ROLE(30, M_ROLE, MqlAdminType.ROLE, "role"),
    GROUP(31, M_GROUP, MqlAdminType.GROUP, "group"),
    EXPRESSION(32, M_EXPRESSION, MqlAdminType.EXPRESSION, "expression", "expr"),
    FORM(33, M_FORM, MqlAdminType.FORM, "form"),
    ALL(34, "All", "all");

    private static Map<String, SlothAdminType> types;
    private static Map<String, SlothAdminType> typesByAlias;

    static {
        types = new HashMap<>();
        for (SlothAdminType aType : SlothAdminType.values()) {
            types.put(aType.getKey(), aType);
        }
        typesByAlias = new HashMap<>();
        for (SlothAdminType aType : SlothAdminType.values()) {
            for (String alias : aType.aliases)
                typesByAlias.put(alias, aType);
        }
    }

    public static SlothAdminType getByKey(String key) {
        return types.get(key);
    }

    public static SlothAdminType getByAlias(String alias) {
        return typesByAlias.get(alias);
    }

    public static boolean isBus(SlothAdminType aType) {
        return aType == BUS || aType == TRIGGER || aType == NUMBER_GENERATOR || aType == OBJECT_GENERATOR;
    }

    public static List<SlothAdminType> getSorted() {
        SlothAdminType[] types = SlothAdminType.values();
        Arrays.sort(types, new Comp());
        return Arrays.asList(types);
    }

    public static List<SlothAdminType> sort(Collection<SlothAdminType> aTypes) {
        List<SlothAdminType> result = new LinkedList<>(aTypes);
        Collections.sort(result, new Comp());
        return result;
    }

    private final int order;
    private final String key;
    private final String mqlKey;
    private final MqlAdminType mqlAdminType;
    private final String[] aliases;
    private final String mqlBusType;

    SlothAdminType(String mqlBusType, int order, String key, String mqlKey, MqlAdminType mqlAdminType, String... aliases) {
        this.order = order;
        this.key = key;
        this.mqlKey = mqlKey;
        this.mqlAdminType = mqlAdminType;
        this.aliases = aliases;
        this.mqlBusType = mqlBusType;
    }

    SlothAdminType(int order, String key, String... aliases) {
        this(null, order, key, null, null, aliases);
    }

    SlothAdminType(int order, String key, MqlAdminType mqlAdminType, String... aliases) {
        this(null, order, key, key, mqlAdminType, aliases);
    }

    public String getMqlBusType() {
        return mqlBusType;
    }

    public String getKey() {
        return key;
    }

    public String getMqlKey() {
        return mqlKey;
    }

    public MqlAdminType getMqlAdminType() {
        return mqlAdminType;
    }

    public static class Comp implements Comparator<SlothAdminType> {
        @Override
        public int compare(SlothAdminType o1, SlothAdminType o2) {
            return Integer.compare(o1.order, o2.order);
        }
    }

    @Override
    public String toString() {
        return getKey();
    }
}
