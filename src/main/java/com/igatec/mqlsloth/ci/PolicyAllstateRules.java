package com.igatec.mqlsloth.ci;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class PolicyAllstateRules {

    private final boolean diffMode;
    private Map<StateRecordKey, StateRecordValue> accessRecords;
    private SortedMap<TriggerKey, TriggerValue> triggers = new TreeMap<>();


    public PolicyAllstateRules(boolean diffMode){
        this.diffMode = diffMode;
        accessRecords = new HashMap<>();
    }

    public boolean isDiffMode() {
        return diffMode;
    }

    public void addAccessRecord(StateRecordKey key, StateRecordValue value) {
        accessRecords.put(key, value);
    }

    public Map <StateRecordKey, StateRecordValue> getAccessRecords(){
        return new HashMap<>(accessRecords);
    }

    public void addTrigger(TriggerKey key, TriggerValue value){
        triggers.put(key, value);
    }
    public SortedMap<TriggerKey, TriggerValue> getTriggers(){
        return new TreeMap<>(triggers);
    }

    protected static void fillDiff(PolicyAllstateRules s1, PolicyAllstateRules s2, PolicyAllstateRules diff){

        /* ACCESS */
        for (StateRecordKey key : s1.accessRecords.keySet()) {
            if (!s2.accessRecords.containsKey(key))
                diff.accessRecords.put(key, null);
        }
        for (StateRecordKey key : s2.accessRecords.keySet()) {
            StateRecordValue v1 = s1.accessRecords.get(key);
            StateRecordValue v2 = s2.accessRecords.get(key);
            if (!v2.equals(v1))
                diff.accessRecords.put(key, v2);
        }

        /* TRIGGERS */
        SortedMap<TriggerKey, TriggerValue> t1 = s1.triggers;
        SortedMap<TriggerKey, TriggerValue> t2 = s2.triggers;
        for (TriggerKey k1: t1.keySet()){
            if (!t2.containsKey(k1))
                diff.triggers.put(k1, null);
        }
        for (TriggerKey k2: t2.keySet()){
            TriggerValue v2 = t2.get(k2);
            TriggerValue v1 = t1.get(k2);
            if (!v2.equals(v1))
                diff.triggers.put(k2, v2);
        }
    }

    public static PolicyAllstateRules buildDiff(PolicyAllstateRules s1, PolicyAllstateRules s2) {
        PolicyAllstateRules diff = new PolicyAllstateRules(true);
        fillDiff(s1, s2, diff);
        return diff;
    }

}
