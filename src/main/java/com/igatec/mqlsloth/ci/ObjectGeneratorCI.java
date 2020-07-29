package com.igatec.mqlsloth.ci;

import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.ci.constants.SlothAdminType;
import com.igatec.mqlsloth.ci.util.BusCIName;
import com.igatec.mqlsloth.ci.util.CIFullName;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class ObjectGeneratorCI extends AdminBusCI {

    public static final String BUS_TYPE = M_OBJECT_GENERATOR;
    public static final String REL_TO_NG = M_NUMBER_GENERATOR;

    public static final String ATTR_NAME_PREFIX = "eService Name Prefix";
    public static final String ATTR_NAME_SUFFIX = "eService Name Suffix";
    public static final String ATTR_TIME_LIMIT = "eService Processing Time Limit";
    public static final String ATTR_RETRY_COUNT = "eService Retry Count";
    public static final String ATTR_RETRY_DELAY = "eService Retry Delay";
    public static final String ATTR_POLICY = "eService Safety Policy";
    public static final String ATTR_VAULT = "eService Safety Vault";

    private static final Set<String> ALL_ATTRS;

    static {
        ALL_ATTRS = new TreeSet<>();
        ALL_ATTRS.add(ATTR_NAME_PREFIX);
        ALL_ATTRS.add(ATTR_NAME_SUFFIX);
        ALL_ATTRS.add(ATTR_TIME_LIMIT);
        ALL_ATTRS.add(ATTR_RETRY_COUNT);
        ALL_ATTRS.add(ATTR_RETRY_DELAY);
        ALL_ATTRS.add(ATTR_POLICY);
        ALL_ATTRS.add(ATTR_VAULT);
    }

    public ObjectGeneratorCI(String name, String revision) {
        this(name, revision, CIDiffMode.TARGET);
    }

    public static CIFullName createCIFullName(String name, String revision) {
        return new CIFullName(SlothAdminType.OBJECT_GENERATOR, new BusCIName(BUS_TYPE, name, revision));
    }

    public static SortedSet<String> getAllAttributesNames() {
        return new TreeSet<>(ALL_ATTRS);
    }

    public ObjectGeneratorCI(String name, String revision, CIDiffMode diffMode) {
        super(
                SlothAdminType.OBJECT_GENERATOR,
                new BusCIName(BUS_TYPE, name, revision),
                OBJECT_GENERATOR_POLICY, diffMode
        );
        if (getDiffMode() == CIDiffMode.TARGET) {
            initTarget();
        } else {
            initDiff();
        }
    }

    private void initDiff() {
        for (String attr : ALL_ATTRS) {
            super.setAttribute(attr, null);
        }
    }

    private void initTarget() {
        for (String attr : ALL_ATTRS) {
            super.setAttribute(attr, "");
        }
    }

    public Boolean isNumberGeneratorConnected() { // TODO debug may be needed
        return getFromConnections().size() > 0;
    }

    public void disconnectNumberGenerator() {
        checkCIConstraint(isNumberGeneratorConnected());
        ConnectionPointer cp = super.getFromConnections().stream().findFirst().get();
        super.reverseFromConnection(cp);
    }

    public void connectNumberGenerator(String name, String revision) {
        checkCIConstraint(name != null, revision != null, !isNumberGeneratorConnected());
        super.addFromConnection(new ConnectionPointer(
                REL_TO_NG, new BusCIName(NumberGeneratorCI.BUS_TYPE, name, revision)
        ));
    }

    public String getNumberGeneratorName() {
        return null; // TODO
    }

    public String getNumberGeneratorRevision() {
        return null; // TODO
    }

    @Override
    public void setAttribute(String key, String value) {
        super.setAttribute(key, value);
    }

    public String getNamePrefix() {
        return getAttribute(ATTR_NAME_PREFIX);
    }

    public void setNamePrefix(String value) {
        checkModeAssertion(value != null, CIDiffMode.TARGET);
        setAttribute(ATTR_NAME_PREFIX, value);
    }

    public String getNameSuffix() {
        return getAttribute(ATTR_NAME_SUFFIX);
    }

    public void setNameSuffix(String value) {
        checkModeAssertion(value != null, CIDiffMode.TARGET);
        setAttribute(ATTR_NAME_SUFFIX, value);
    }

    public String getProcessingTimeLimit() {
        return getAttribute(ATTR_TIME_LIMIT);
    }

    public void setProcessingTimeLimit(String value) {
        checkModeAssertion(value != null, CIDiffMode.TARGET);
        setAttribute(ATTR_TIME_LIMIT, value);
    }

    public String getRetryCount() {
        return getAttribute(ATTR_RETRY_COUNT);
    }

    public void setRetryCount(String value) {
        checkModeAssertion(value != null, CIDiffMode.TARGET);
        setAttribute(ATTR_RETRY_COUNT, value);
    }

    public String getRetryDelay() {
        return getAttribute(ATTR_RETRY_DELAY);
    }

    public void setRetryDelay(String value) {
        checkModeAssertion(value != null, CIDiffMode.TARGET);
        setAttribute(ATTR_RETRY_DELAY, value);
    }

    public String getSafetyPolicy() {
        return getAttribute(ATTR_POLICY);
    }

    public void setSafetyPolicy(String value) {
        checkModeAssertion(value != null, CIDiffMode.TARGET);
        setAttribute(ATTR_POLICY, value);
    }

    public String getSafetyVault() {
        return getAttribute(ATTR_VAULT);
    }

    public void setSafetyVault(String value) {
        checkModeAssertion(value != null, CIDiffMode.TARGET);
        setAttribute(ATTR_VAULT, value);
    }

    @Override
    public ObjectGeneratorCI buildDiff(AbstractCI newCI) {
        BusCIName tnr = (BusCIName) getCIName();
        ObjectGeneratorCI ci = (ObjectGeneratorCI) newCI;
        ObjectGeneratorCI diff = new ObjectGeneratorCI(tnr.getName(), tnr.getRevision(), CIDiffMode.DIFF);
        fillDiffCI(ci, diff);
        return diff;
    }

    @Override
    public AbstractCI buildDefaultCI() {
        BusCIName tnr = (BusCIName) getCIName();
        return new ObjectGeneratorCI(tnr.getName(), tnr.getRevision());
    }
}
