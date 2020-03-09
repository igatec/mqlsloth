package com.igatec.mqlsloth.ci;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class PolicySignature {

    private final String name;
    private String branch;
    private final Set<String> approve = new HashSet<>();
    private final Set<String> ignore = new HashSet<>();
    private final Set<String> reject = new HashSet<>();
    private String filter;

    public PolicySignature(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public Set<String> getApprove() {
        return new HashSet<>(approve);
    }

    public boolean addApprove(String value){
        return approve.add(value);
    }

    public Set<String> getIgnore() {
        return new HashSet<>(ignore);
    }

    public boolean addIgnore(String value){
        return ignore.add(value);
    }

    public Set<String> getReject() {
        return new HashSet<>(reject);
    }

    public boolean addReject(String value){
        return reject.add(value);
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PolicySignature that = (PolicySignature) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(branch, that.branch) &&
                Objects.equals(approve, that.approve) &&
                Objects.equals(ignore, that.ignore) &&
                Objects.equals(reject, that.reject) &&
                Objects.equals(filter, that.filter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, branch, approve, ignore, reject, filter);
    }
}
