package com.igatec.mqlsloth.ci;

import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.ci.constants.SlothAdminType;
import com.igatec.mqlsloth.ci.util.BusCIName;
import com.igatec.mqlsloth.ci.util.CIFullName;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class TriggerCI extends AdminBusCI {

    public static final String BUS_TYPE = M_TRIGGER_INPUT;
    public static final String BUS_POLICY = "eService Trigger Program Policy";
    public static final String STATE_ACTIVE = "Active";
    public static final String STATE_INACTIVE = "Inactive";

    public static final String ATTR_ERROR_TYPE = "eService Error Type";
    public static final String ATTR_PROGRAM_NAME = "eService Program Name";
    public static final String ATTR_METHOD_NAME = "eService Method Name";
    public static final String ATTR_CONSTRUCTOR_ARGUMENTS = "eService Constructor Arguments";
    public static final String ATTR_SEQUENCE_NUMBER = "eService Sequence Number";
    public static final String ATTR_TARGET_STATES = "eService Target States";
    public static final String ATTR_PROGRAM_ARG_PREFIX = "eService Program Argument ";
    public static final String ATTR_PROGRAM_ARG_DESC_PREFIX = "eService Program Argument Desc ";
    private static final String ATTR_ERROR_TYPE_DEFAULT_VALUE = "Error";
    private static final Set<Integer> ALL_ARGS_INDEXES;
    private static final Set<String> ALL_ATTRS;

    private static final int FIRST_ARG = 1;
    private static final int LAST_ARG = 1;

    static {
        ALL_ARGS_INDEXES = new TreeSet<>();
        for (int i = FIRST_ARG; i <= LAST_ARG; i++) {
            ALL_ARGS_INDEXES.add(i);
        }
        ALL_ATTRS = new TreeSet<>();
        ALL_ATTRS.add(ATTR_ERROR_TYPE);
        ALL_ATTRS.add(ATTR_PROGRAM_NAME);
        ALL_ATTRS.add(ATTR_METHOD_NAME);
        ALL_ATTRS.add(ATTR_CONSTRUCTOR_ARGUMENTS);
        ALL_ATTRS.add(ATTR_SEQUENCE_NUMBER);
        ALL_ATTRS.add(ATTR_TARGET_STATES);
        for (Integer i : ALL_ARGS_INDEXES) {
            ALL_ATTRS.add(ATTR_PROGRAM_ARG_PREFIX + i);
            ALL_ATTRS.add(ATTR_PROGRAM_ARG_DESC_PREFIX + i);
        }
    }

    public TriggerCI(String name, String revision) {
        this(name, revision, CIDiffMode.TARGET);
    }

    public static CIFullName createCIFullName(String name, String revision) {
        return new CIFullName(SlothAdminType.TRIGGER, new BusCIName(BUS_TYPE, name, revision));
    }

    public static SortedSet<String> getAllAttributesNames() {
        return new TreeSet<>(ALL_ATTRS);
    }

    public TriggerCI(String name, String revision, CIDiffMode diffMode) {
        super(
                SlothAdminType.TRIGGER,
                new BusCIName(BUS_TYPE, name, revision),
                BUS_POLICY, diffMode
        );
        if (getDiffMode() == CIDiffMode.TARGET) {
            initTarget();
        } else {
            initDiff();
        }

    }

    private void initDiff() {
        setActive(null);
        for (String attr : ALL_ATTRS) {
            super.setAttribute(attr, null);
        }
    }

    private void initTarget() {
        setActive(false);
        for (String attr : ALL_ATTRS) {
            super.setAttribute(attr, "");
        }
        setErrorType(ATTR_ERROR_TYPE_DEFAULT_VALUE);
    }

    private String getProgramArgKey(int i) {
        checkCIConstraint(
                "Trigger can contain arguments: [" + FIRST_ARG + " ... " + LAST_ARG + "]",
                i >= FIRST_ARG, i <= LAST_ARG
        );
        return ATTR_PROGRAM_ARG_PREFIX + i;
    }

    private String getProgramArgDescKey(int i) {
        checkCIConstraint(
                "Trigger can contain arguments: [" + FIRST_ARG + " ... " + LAST_ARG + "]",
                i >= FIRST_ARG, i <= LAST_ARG
        );
        return ATTR_PROGRAM_ARG_DESC_PREFIX + i;
    }

    public void setProgramArg(int i, String value, String description) {
        checkModeAssertion(value != null, CIDiffMode.TARGET);
        checkModeAssertion(description != null, CIDiffMode.TARGET);
        setAttribute(getProgramArgKey(i), value);
        setAttribute(getProgramArgDescKey(i), description);
    }

    public String getProgramArg(int i) {
        return getAttribute(getProgramArgKey(i));
    }

    public String getProgramArgDesc(int i) {
        return getAttribute(getProgramArgDescKey(i));
    }

    @Override
    public void setAttribute(String key, String value) {
        super.setAttribute(key, value);
    }

    public String getErrorType() {
        return getAttribute(ATTR_ERROR_TYPE);
    }

    public void setErrorType(String value) {
        checkModeAssertion(value != null, CIDiffMode.TARGET);
        setAttribute(ATTR_ERROR_TYPE, value);
    }

    public String getProgram() {
        return getAttribute(ATTR_PROGRAM_NAME);
    }

    public void setProgram(String value) {
        checkModeAssertion(value != null, CIDiffMode.TARGET);
        setAttribute(ATTR_PROGRAM_NAME, value);
    }

    public String getMethod() {
        return getAttribute(ATTR_METHOD_NAME);
    }

    public void setMethod(String value) {
        checkModeAssertion(value != null, CIDiffMode.TARGET);
        setAttribute(ATTR_METHOD_NAME, value);
    }

    public String getTargetStates() {
        return getAttribute(ATTR_TARGET_STATES);
    }

    public void setTargetStates(String value) {
        checkModeAssertion(value != null, CIDiffMode.TARGET);
        setAttribute(ATTR_TARGET_STATES, value);
    }

    public String getSequenceNumber() {
        return getAttribute(ATTR_SEQUENCE_NUMBER);
    }

    public void setSequenceNumber(String value) {
        checkModeAssertion(value != null, CIDiffMode.TARGET);
        setAttribute(ATTR_SEQUENCE_NUMBER, value);
    }

    public String getConstructorArgs() {
        return getAttribute(ATTR_CONSTRUCTOR_ARGUMENTS);
    }

    public void setConstructorArgs(String value) {
        checkModeAssertion(value != null, CIDiffMode.TARGET);
        setAttribute(ATTR_CONSTRUCTOR_ARGUMENTS, value);
    }

    public Boolean isActive() {
        String active = getState();
        return (active == null) ? null : STATE_ACTIVE.equals(active);
    }

    public void setActive(Boolean active) {
        String state = (active == null) ? null : (active ? STATE_ACTIVE : STATE_INACTIVE);
        setState(state);
    }

    @Override
    public TriggerCI buildDiff(AbstractCI newCI) {
        BusCIName tnr = (BusCIName) getCIName();
        TriggerCI ci = (TriggerCI) newCI;
        TriggerCI diff = new TriggerCI(tnr.getName(), tnr.getRevision(), CIDiffMode.DIFF);
        fillDiffCI(ci, diff);
        return diff;
    }

    @Override
    public AbstractCI buildDefaultCI() {
        BusCIName tnr = (BusCIName) getCIName();
        return new TriggerCI(tnr.getName(), tnr.getRevision());
    }
}
