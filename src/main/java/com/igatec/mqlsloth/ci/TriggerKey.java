package com.igatec.mqlsloth.ci;

import com.igatec.mqlsloth.ci.constants.TriggerAction;
import com.igatec.mqlsloth.ci.constants.TriggerPhase;

public class TriggerKey implements Comparable<TriggerKey> {

    private final TriggerAction action;
    private final TriggerPhase phase;

    public TriggerKey(String action, String phase) {
        this.action = TriggerAction.get(action);
        this.phase = TriggerPhase.get(phase);
    }

    public TriggerAction getAction() {
        return action;
    }

    public TriggerPhase getPhase() {
        return phase;
    }

    public static TriggerKey fromSpacedString(String s) {
        String[] items = s.split(" ");
        return new TriggerKey(items[0], items[1]);
    }

    public static TriggerKey fromCamelCase(String s) {
        char[] chars = s.toCharArray();
        int index = -1;
        for (int i = 0; i < chars.length; i++) {
            if (Character.isUpperCase(chars[i])) {
                index = i;
            }
        }
        return new TriggerKey(
                s.substring(0, index).toLowerCase(),
                s.substring(index).toLowerCase()
        );
    }

    @Override
    public int compareTo(TriggerKey o) {
        int c = action.compareTo(o.action);
        if (c != 0) {
            return c;
        }
        return phase.compareTo(o.phase);
    }

    public String toSpacedString() {
        return action + " " + phase;
    }

    public String toCamelCase() {
        char[] a = action.toString().toCharArray();
        a[0] = Character.toUpperCase(a[0]);
        char[] p = phase.toString().toCharArray();
        p[0] = Character.toUpperCase(p[0]);
        return new String(a) + new String(p);
    }

}
