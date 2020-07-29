package com.igatec.mqlsloth.ci;

import com.igatec.mqlsloth.ci.constants.AccessValue;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class StateRecordValue {

    private final Set<AccessValue> accessValues;
    private final List<String> extraInfo;
    private final String filter;

    public StateRecordValue(Collection<String> accessValues, List<String> extraInfo, String filter) {
        this.accessValues = new HashSet<>();
        for (String s : accessValues) {
            AccessValue value = AccessValue.get(s);
            if (value == null) {
                throw new IllegalArgumentException("Access value '" + s + "' is not valid");
            }
            this.accessValues.add(value);
        }
        this.extraInfo = extraInfo;
        this.filter = filter;
    }

    public Set<AccessValue> getAccessValues() {
        return new HashSet<>(accessValues);
    }

    public List<String> getExtraInfo() {
        return extraInfo;
    }

    public String getFilter() {
        return filter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StateRecordValue that = (StateRecordValue) o;
        return Objects.equals(accessValues, that.accessValues)
                && Objects.equals(extraInfo, that.extraInfo)
                && Objects.equals(filter, that.filter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessValues, extraInfo, filter);
    }
}
