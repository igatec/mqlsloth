package com.igatec.mqlsloth.ci;

import com.igatec.mqlsloth.script.MqlKeywords;
import com.igatec.mqlsloth.script.MqlUtil;
import com.igatec.mqlsloth.util.CollectionComparator;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class StateRecordKey implements Comparable<StateRecordKey> {

    private List<String> modifiers;
    private String user;
    private String key;

    public StateRecordKey(List<String> modifiers, String user, String key) {
        this.modifiers = modifiers;
        this.user = user;
        this.key = key;
    }

    public List<String> getModifiers() {
        return modifiers;
    }

    public String getUser() {
        return user;
    }

    public String getKey() {
        return key;
    }

    public List<String> toList() {
        List<String> result = new LinkedList<>(modifiers);
        if (user != null) {
            result.add(MqlKeywords.M_USER);
            result.add(MqlUtil.qWrap(user));
        }
        if (key != null) {
            result.add(MqlKeywords.M_KEY);
            result.add(MqlUtil.qWrap(key));
        }
        return result;
    }

    public String[] toArray() {
        return toList().toArray(new String[0]);
    }

    @Override
    public int compareTo(StateRecordKey o) {
        int c = new CollectionComparator<String>().compare(modifiers, o.modifiers);
        if (c != 0) {
            return c;
        }
        if (!Objects.equals(key, o.key)) {
            return Objects.compare(key, o.key, String::compareTo);
        }
        if (Objects.equals(key, o.key)) {
            return 0;
        }
        if (key == null) {
            return -1;
        }
        if (o.key == null) {
            return 1;
        }
        return key.compareTo(o.key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StateRecordKey that = (StateRecordKey) o;
        return compareTo(that) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(modifiers, user, key);
    }

}
