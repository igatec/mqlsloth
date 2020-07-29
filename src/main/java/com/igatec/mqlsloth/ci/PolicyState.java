package com.igatec.mqlsloth.ci;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PolicyState extends PolicyAllstateRules {

    private final String name;
    private Boolean minorRevisionable;
    private Boolean majorRevisionable;
    private Boolean versionable;
    private Boolean published;
    private Boolean checkoutHistory;
    Set<String> signaturesToRemove = new HashSet<>(); // workaround, valid only in diff mode

    private Map<String, PolicySignature> signatures;

    public PolicyState(String name, boolean diffMode) {
        super(diffMode);
        this.name = name;
        if (isDiffMode()) {
            initDiff();
        } else {
            initTarget();
        }
    }

    private void initDiff() {
        minorRevisionable = null;
        majorRevisionable = null;
        versionable = null;
        published = null;
        checkoutHistory = null;
        signatures = null;
    }

    private void initTarget() {
        minorRevisionable = true;
        majorRevisionable = true;
        versionable = true;
        published = false;
        checkoutHistory = true;
        signatures = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public Boolean isMinorRevisionable() {
        return minorRevisionable;
    }

    public void setMinorRevisionable(Boolean minorRevisionable) {
        this.minorRevisionable = minorRevisionable;
    }

    public Boolean isMajorRevisionable() {
        return majorRevisionable;
    }

    public void setMajorRevisionable(Boolean majorRevisionable) {
        this.majorRevisionable = majorRevisionable;
    }

    public Boolean isVersionable() {
        return versionable;
    }

    public void setVersionable(Boolean versionable) {
        this.versionable = versionable;
    }

    public Boolean isPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public Boolean isCheckoutHistory() {
        return checkoutHistory;
    }

    public void setCheckoutHistory(Boolean checkoutHistory) {
        this.checkoutHistory = checkoutHistory;
    }

    public static PolicyState buildDiff(PolicyState s1, PolicyState s2) {
        PolicyState diff = new PolicyState(s1.name, true);
        PolicyAllstateRules.fillDiff(s1, s2, diff);
        if (!s2.minorRevisionable.equals(s1.minorRevisionable)) {
            diff.minorRevisionable = s2.minorRevisionable;
        }
        if (!s2.majorRevisionable.equals(s1.majorRevisionable)) {
            diff.majorRevisionable = s2.majorRevisionable;
        }
        if (!s2.versionable.equals(s1.versionable)) {
            diff.versionable = s2.versionable;
        }
        if (!s2.published.equals(s1.published)) {
            diff.published = s2.published;
        }
        if (!s2.checkoutHistory.equals(s1.checkoutHistory)) {
            diff.checkoutHistory = s2.checkoutHistory;
        }
        if (!s2.signatures.equals(s1.signatures)) {
            diff.signatures = s2.signatures; // TODO need deepClone()
            diff.signaturesToRemove.addAll(s1.signatures.values().stream()
                    .map(PolicySignature::getName).collect(Collectors.toSet())
            );
        }
        return diff;
    }

    public List<PolicySignature> getSignatures() {
        return signatures == null ? null : new LinkedList<>(signatures.values());
    }

    public void setSignatures(Collection<PolicySignature> signatures) {
        this.signatures = new HashMap<>();
        signatures.forEach(v -> this.signatures.put(v.getName(), v));
    }

    public void addSignature(PolicySignature signature) {
        if (signatures == null) {
            signatures = new HashMap<>();
        }
        signatures.put(signature.getName(), signature);
    }
}
