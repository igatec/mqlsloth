package com.igatec.mqlsloth.ci;

import com.igatec.mqlsloth.ci.annotation.ModBooleanProvider;
import com.igatec.mqlsloth.ci.annotation.ModStringProvider;
import com.igatec.mqlsloth.ci.annotation.ModStringSetProvider;
import com.igatec.mqlsloth.ci.constants.AccessValue;
import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.ci.constants.ScriptPriority;
import com.igatec.mqlsloth.ci.constants.SlothAdminType;
import com.igatec.mqlsloth.ci.util.CIFullName;
import com.igatec.mqlsloth.script.ModChunk;
import com.igatec.mqlsloth.script.MqlUtil;
import com.igatec.mqlsloth.script.ScriptChunk;
import com.igatec.mqlsloth.util.DiffList;
import com.igatec.mqlsloth.util.ReversibleSet;
import com.igatec.mqlsloth.util.SlothSet;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class PolicyCI extends AdminObjectCI {
    private ReversibleSet<String> types;
    private String store;
    private ReversibleSet<String> formats; // TODO add to parsers
    private String defaultFormat; // TODO add to parsers
    private Boolean enforceLocking;
    private String minorSequence;
    private List<PolicyState> states;
    private List<PolicyAllstateRules> allStates; // This list contains max 1 item

    public PolicyCI(String name) {
        this(name, CIDiffMode.TARGET);
    }

    private PolicyCI(String name, CIDiffMode diffMode) {
        super(SlothAdminType.POLICY, name, diffMode);
        if (!isDiffMode()) {
            initTarget();
        } else {
            initDiff();
        }
    }

    private void initTarget() {
        types = new SlothSet<>(false);
        store = "";
        formats = new SlothSet<>(false);
        defaultFormat = "";
        enforceLocking = false;
        minorSequence = "";
        states = new ArrayList<>();
        allStates = new LinkedList<>();
    }

    private void initDiff() {
        types = new SlothSet<>(true);
        store = null;
        formats = new SlothSet<>(true);
        defaultFormat = null;
        enforceLocking = null;
        minorSequence = null;
        states = null;
        allStates = null;
    }

    public void setAllStatesRules(PolicyAllstateRules rules) {
        allStates = new LinkedList<>();
        allStates.add(rules);
    }

    public List<PolicyAllstateRules> getAllStatesRules() {
        return new LinkedList<>(allStates);
    }

    public PolicyState addState(String stateName) {
        checkModeAssertion(CIDiffMode.TARGET);
        boolean noDuplicate = states.stream().filter(s -> stateName.equals(s.getName())).collect(Collectors.toList()).isEmpty();
        checkCIConstraint("Policy cannot contain states with same name", noDuplicate);
        PolicyState state = new PolicyState(stateName, isDiffMode());
        states.add(state);
        return state;
    }

    public PolicyState getState(String stateName) {
        List<PolicyState> list = states.stream().filter(s -> stateName.equals(s.getName())).collect(Collectors.toList());
        return list.isEmpty() ? null : list.get(0);
    }

    public List<PolicyState> getStates() {
        return new ArrayList<>(states);
    }

    @ModStringSetProvider(value = M_TYPE, addPriority = SP_AFTER_ADMIN_CREATION_1)
    public ReversibleSet<String> getTypes() {
        return new SlothSet<>(types, isDiffMode());
    }

    public Boolean addType(String value) {
        return types.add(value);
    }

    public boolean reverseType(String value) {
        checkModeAssertion(CIDiffMode.DIFF);
        return types.reverse(value);
    }

    @ModStringProvider(M_STORE)
    public String getStore() {
        return store;
    }

    public void setStore(String value) {
        checkModeAssertion(value != null, CIDiffMode.TARGET);
//        checkCIConstraint("Store cannot be removed from policy", !isDiffMode() && (value == null || !value.isEmpty()));
        this.store = value;
    }

    @ModStringSetProvider(value = M_FORMAT, addPriority = SP_AFTER_ADMIN_CREATION_1)
    public ReversibleSet<String> getFormats() {
        return new SlothSet<>(formats, isDiffMode());
    }

    public Boolean addFormat(String value) {
        return formats.add(value);
    }

    public boolean reverseFormat(String value) {
        checkModeAssertion(CIDiffMode.DIFF);
        return formats.reverse(value);
    }

    @ModStringProvider(M_DEFAULT_FORMAT)
    public String getDefaultFormat() {
        return defaultFormat;
    }

    public void setDefaultFormat(String value) {
        checkModeAssertion(value != null, CIDiffMode.TARGET);
        this.defaultFormat = value;
    }

    @ModBooleanProvider(M_ENFORCE)
    public Boolean isLockingEnforced() {
        return enforceLocking;
    }

    public void setEnforceLocking(Boolean value) {
        checkModeAssertion(value != null, CIDiffMode.TARGET);
        enforceLocking = value;
    }

    @ModStringProvider(M_MINOR_SEQUENCE)
    public String getMinorSequence() {
        return minorSequence;
    }

    public void setMinorSequence(String value) {
        checkModeAssertion(value != null, CIDiffMode.TARGET);
        this.minorSequence = value;
    }

    @Override
    protected void fillDiffCI(AbstractCI newCI, AbstractCI diffCI) {
        super.fillDiffCI(newCI, diffCI);
        PolicyCI newCastedCI = (PolicyCI) newCI;
        PolicyCI diffCastedCI = (PolicyCI) diffCI;

        ReversibleSet<String> oldTypes = getTypes();
        ReversibleSet<String> newTypes = newCastedCI.getTypes();
        for (String value : SlothSet.itemsToRemove(oldTypes, newTypes)) {
            diffCastedCI.reverseType(value);
        }
        for (String value : SlothSet.itemsToAdd(oldTypes, newTypes)) {
            diffCastedCI.addType(value);
        }
        String store = newCastedCI.getStore();
        if (store != null && !store.equals(getStore())) {
            diffCastedCI.setStore(store);
        }

        ReversibleSet<String> oldFormats = getFormats();
        ReversibleSet<String> newFormats = newCastedCI.getFormats();

        for (String value : SlothSet.itemsToRemove(oldFormats, newFormats)) {
            diffCastedCI.reverseFormat(value);
        }
        for (String value : SlothSet.itemsToAdd(oldFormats, newFormats)) {
            diffCastedCI.addFormat(value);
        }
        String defaultFormat = newCastedCI.getDefaultFormat();
        if (defaultFormat != null && !defaultFormat.equals(getDefaultFormat())) {
            diffCastedCI.setDefaultFormat(defaultFormat);
        }
        Boolean isLockingInforce = newCastedCI.isLockingEnforced();
        if (isLockingInforce != null && !isLockingInforce.equals(isLockingEnforced())) {
            diffCastedCI.setEnforceLocking(isLockingInforce);
        }
        String minorSequence = newCastedCI.getMinorSequence();
        if (minorSequence != null && !minorSequence.equals(getMinorSequence())) {
            diffCastedCI.setMinorSequence(minorSequence);
        }
        List<PolicyState> sl1 = getStates();
        List<PolicyState> sl2 = newCastedCI.getStates();
        diffCastedCI.states = new DiffList<>(
                sl1, sl2, PolicyState::getName, PolicyState::buildDiff
        );
        List<PolicyAllstateRules> allStates1 = getAllStatesRules();
        List<PolicyAllstateRules> allStates2 = newCastedCI.getAllStatesRules();
        diffCastedCI.allStates = new DiffList<>(
                allStates1, allStates2, item -> 0, PolicyAllstateRules::buildDiff
        );
    }

    @Override
    public AbstractCI buildDiff(AbstractCI newCI) {
        PolicyCI ci = (PolicyCI) newCI;
        PolicyCI diff = new PolicyCI(getName(), CIDiffMode.DIFF);
        fillDiffCI(ci, diff);
        return diff;
    }

    @Override
    public AbstractCI buildDefaultCI() {
        return new PolicyCI(getName());
    }

    // todo uncomment checkstyle ignore and refactor
    // CHECKSTYLE.OFF: MethodLength
    @Override
    public List<ScriptChunk> buildUpdateScript() {
        CIFullName fName = getCIFullName();
        List<ScriptChunk> chunks = super.buildUpdateScript();

        DiffList<PolicyState> states = (DiffList<PolicyState>) this.states;
        int lastIndex = states.size() - 1;
        int createAndRemoveStatePriority = ScriptPriority.SP_AFTER_ADMIN_CREATION_1;
        for (int i = lastIndex; i >= 0; i--) {
            PolicyState state = states.get(i);
            DiffList.Mode mode = states.getMode(i);
            if (mode == DiffList.Mode.CREATE) {
                ScriptChunk chunk;
                if (i == lastIndex) {
                    chunk = new ModChunk(fName, createAndRemoveStatePriority++,
                            M_ADD, M_STATE, state.getName()
                    );
                } else {
                    PolicyState nextState = states.get(i + 1);
                    String nextStateName = nextState.getName();
                    chunk = new ModChunk(fName, createAndRemoveStatePriority++,
                            M_ADD, M_STATE, state.getName(), M_BEFORE, nextStateName
                    );
                }
                chunks.add(chunk);
            }
        }

        for (int i = 0; i <= lastIndex; i++) {
            PolicyState state = states.get(i);
            DiffList.Mode mode = states.getMode(i);

            if (mode == DiffList.Mode.REMOVE) {
                chunks.add(new ModChunk(fName, createAndRemoveStatePriority++, M_REMOVE, M_STATE, state.getName()));
                continue;
            }

            Optional.ofNullable(state.isMinorRevisionable()).ifPresent(s -> {
                chunks.add(modStateChunk(state, M_MINOR_REVISION, s ? M_TRUE : M_FALSE));
            });
            Optional.ofNullable(state.isMajorRevisionable()).ifPresent(s -> {
                chunks.add(modStateChunk(state, M_MAJOR_REVISION, s ? M_TRUE : M_FALSE));
            });
            Optional.ofNullable(state.isVersionable()).ifPresent(s -> {
                chunks.add(modStateChunk(state, M_VERSION, s ? M_TRUE : M_FALSE));
            });
            Optional.ofNullable(state.isPublished()).ifPresent(s -> {
                chunks.add(modStateChunk(state, M_PUBLISHED, s ? M_TRUE : M_FALSE));
            });
            Optional.ofNullable(state.isCheckoutHistory()).ifPresent(s -> {
                chunks.add(modStateChunk(state, M_CHECKOUT_HISTORY, s ? M_TRUE : M_FALSE));
            });

            Map<StateRecordKey, StateRecordValue> accessRecords = state.getAccessRecords();
            for (StateRecordKey key : accessRecords.keySet()) {
                List<String> keyList = key.toList();

                LinkedList<String> remList = new LinkedList<>(keyList);
                remList.addFirst(M_REMOVE);
                remList.add(M_ALL);
                chunks.add(modStateChunk(state, ScriptPriority.SP_AFTER_ADMIN_CREATION_502, remList.toArray(new String[0])));

                StateRecordValue value = accessRecords.get(key);
                if (value != null) {
                    List<String> cmdList = new LinkedList<>();
                    for (AccessValue access : value.getAccessValues()) {
                        cmdList.add(M_ADD);
                        cmdList.addAll(keyList);
                        cmdList.add(access.toString());
                    }
                    cmdList.addAll(value.getExtraInfo());
                    Optional.ofNullable(value.getFilter()).ifPresent(filter -> {
                        cmdList.add(M_FILTER);
                        cmdList.add(MqlUtil.qWrap(filter));
                    });
                    chunks.add(modStateChunk(state, ScriptPriority.SP_AFTER_ADMIN_CREATION_503, cmdList.toArray(new String[0])));
                }
            }

            Map<TriggerKey, TriggerValue> triggers = state.getTriggers();
            for (TriggerKey key : triggers.keySet()) {
                TriggerValue value = triggers.get(key);
                if (value == null) {
                    chunks.add(modStateChunk(state, ScriptPriority.SP_AFTER_ADMIN_CREATION_503,
                            M_REMOVE, M_TRIGGER, key.getAction().toString(), key.getPhase().toString()));
                } else {
                    chunks.add(modStateChunk(state, ScriptPriority.SP_AFTER_ADMIN_CREATION_503,
                            M_ADD, M_TRIGGER, key.getAction().toString(), key.getPhase().toString(),
                            MqlUtil.qWrap(value.getProgram()), M_INPUT, MqlUtil.qWrap(value.getInput())
                    ));
                }
            }

            List<PolicySignature> sigs = state.getSignatures();
            if (sigs != null) {
                Collection<String> sigsToRemove = state.signaturesToRemove;
                List<String> removeCmds = new LinkedList<>();
                sigsToRemove.forEach(sigName -> {
                    removeCmds.add(M_REMOVE);
                    removeCmds.add(M_SIGNATURE);
                    removeCmds.add(sigName);
                });
                chunks.add(modStateChunk(state, ScriptPriority.SP_AFTER_ADMIN_CREATION_503,
                        removeCmds.toArray(new String[0])));
                for (int j = 0; j < sigs.size(); j++) {
                    PolicySignature sig = sigs.get(j);
                    List<String> cmd = new LinkedList<>();
                    cmd.add(M_ADD);
                    cmd.add(M_SIGNATURE);
                    cmd.add(sig.getName());
                    Optional.ofNullable(sig.getBranch()).ifPresent(v -> {
                        cmd.add(M_BRANCH);
                        cmd.add(v);
                    });
                    sig.getApprove().forEach(v -> {
                        cmd.add(M_APPROVE);
                        cmd.add(v);
                    });
                    sig.getIgnore().forEach(v -> {
                        cmd.add(M_IGNORE);
                        cmd.add(v);
                    });
                    sig.getReject().forEach(v -> {
                        cmd.add(M_REJECT);
                        cmd.add(v);
                    });
                    Optional.ofNullable(sig.getFilter()).ifPresent(v -> {
                        cmd.add(M_FILTER);
                        cmd.add(v);
                    });
                    chunks.add(modStateChunk(state, ScriptPriority.SP_AFTER_ADMIN_CREATION_504,
                            cmd.toArray(new String[0])));
                }
            }
        }

        /* ALL STATES */

        DiffList<PolicyAllstateRules> allStates = (DiffList<PolicyAllstateRules>) this.allStates;
        if (allStates != null && !allStates.isEmpty()) {
            PolicyAllstateRules state = allStates.get(0);
            DiffList.Mode mode = allStates.getMode(0);
            if (mode == DiffList.Mode.REMOVE) {
                chunks.add(new ModChunk(fName, createAndRemoveStatePriority++, M_REMOVE, M_ALL_STATE));
            } else {
                if (mode == DiffList.Mode.CREATE) {
                    chunks.add(new ModChunk(fName, createAndRemoveStatePriority++, M_ADD, M_ALL_STATE));
                }

                Map<StateRecordKey, StateRecordValue> accessRecords = state.getAccessRecords();
                for (StateRecordKey key : accessRecords.keySet()) {
                    List<String> keyList = key.toList();

                    LinkedList<String> remList = new LinkedList<>(keyList);
                    remList.addFirst(M_REMOVE);
                    remList.add(M_ALL);
                    chunks.add(modAllStateChunk(ScriptPriority.SP_AFTER_ADMIN_CREATION_502, remList.toArray(new String[0])));

                    StateRecordValue value = accessRecords.get(key);
                    if (value != null) {
                        List<String> cmdList = new LinkedList<>();
                        for (AccessValue access : value.getAccessValues()) {
                            cmdList.add(M_ADD);
                            cmdList.addAll(keyList);
                            cmdList.add(access.toString());
                        }
                        cmdList.addAll(value.getExtraInfo());
                        Optional.ofNullable(value.getFilter()).ifPresent(filter -> {
                            cmdList.add(M_FILTER);
                            cmdList.add(MqlUtil.qWrap(filter));
                        });
                        chunks.add(modAllStateChunk(ScriptPriority.SP_AFTER_ADMIN_CREATION_503, cmdList.toArray(new String[0])));
                    }
                }

                Map<TriggerKey, TriggerValue> triggers = state.getTriggers();
                for (TriggerKey key : triggers.keySet()) {
                    TriggerValue value = triggers.get(key);
                    if (value == null) {
                        chunks.add(
                                modAllStateChunk(
                                        ScriptPriority.SP_AFTER_ADMIN_CREATION_503,
                                        M_REMOVE,
                                        M_TRIGGER,
                                        key.getAction().toString(),
                                        key.getPhase().toString()
                                )
                        );
                    } else {
                        chunks.add(modAllStateChunk(
                                ScriptPriority.SP_AFTER_ADMIN_CREATION_503,
                                M_ADD, M_TRIGGER,
                                key.getAction().toString(),
                                key.getPhase().toString(),
                                MqlUtil.qWrap(value.getProgram()),
                                M_INPUT,
                                MqlUtil.qWrap(value.getInput())
                        ));
                    }
                }
            }
        }
        return chunks;
    }
    // CHECKSTYLE.OFF: MethodLength

    private ModChunk modStateChunk(PolicyState state, String... cmdParam) {
        return modStateChunk(state, ScriptPriority.SP_AFTER_ADMIN_CREATION_502, cmdParam);
    }

    private ModChunk modStateChunk(PolicyState state, int priority, String... cmdParam) {
        String[] arr = new String[]{M_STATE, state.getName()};
        arr = ArrayUtils.addAll(arr, cmdParam);
        return new ModChunk(getCIFullName(), priority, arr);
    }

    private ModChunk modAllStateChunk(int priority, String... cmdParam) {
        String[] arr = new String[]{M_ALL_STATE};
        arr = ArrayUtils.addAll(arr, cmdParam);
        return new ModChunk(getCIFullName(), priority, arr);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> fieldsValues = super.toMap();
        fieldsValues.put(Y_TYPES, new TreeSet<>(getTypes()));
        fieldsValues.put(Y_ENFORCE_LOCKING, isLockingEnforced());
        fieldsValues.put(Y_STORE, getStore());
        fieldsValues.put(Y_MINOR_SEQUENCE, getMinorSequence());
        fieldsValues.put(Y_DEFAULT_FORMAT, getDefaultFormat());
        fieldsValues.put(Y_FORMATS, getFormats());

        List<Map<String, Object>> stateMaps = getStates().stream().map(this::stateToMap).collect(Collectors.toList());

        if (allStates != null && !allStates.isEmpty()) {
            fieldsValues.put(Y_ALL_STATES, stateToMap(allStates.get(0)));
        }
        fieldsValues.put(Y_STATES, stateMaps);
        return fieldsValues;
    }

    private Map<String, Object> stateToMap(PolicyAllstateRules state) {
        Map<String, Object> result = new LinkedHashMap<>();
        if (state instanceof PolicyState) {
            PolicyState st = (PolicyState) state;
            result.put(Y_NAME, st.getName());
            result.put(Y_MINOR_REVISIONABLE, st.isMinorRevisionable());
            result.put(Y_MAJOR_REVISIONABLE, st.isMajorRevisionable());
            result.put(Y_VERSIONABLE, st.isVersionable());
            result.put(Y_PUBLISHED, st.isPublished());
            result.put(Y_CHECKOUT_HISTORY, st.isCheckoutHistory());
        }

        Map<String, List<String>> aRecords = new LinkedHashMap<>();
        Map<StateRecordKey, StateRecordValue> accessRecords = state.getAccessRecords();
        for (StateRecordKey recordKey : accessRecords.keySet()) {
            String k = "";
            List<String> modifs = recordKey.getModifiers();
            if (modifs.size() > 0) {
                k = StringUtils.join(modifs, " ");
            }
            String user = recordKey.getUser();
            if (k != null && !k.isEmpty() && user != null) {
                k += " ";
            }
            if (user != null) {
                k += Y_USER + " " + user;
            }

            StringBuilder line = new StringBuilder();
            Optional.ofNullable(recordKey.getKey()).ifPresent(key -> {
                line.append(Y_KEY);
                line.append(" ");
                line.append(key);
                line.append(" ");
            });
            StateRecordValue recordValue = accessRecords.get(recordKey);
            line.append(StringUtils.join(recordValue.getAccessValues(), ","));
            recordValue.getExtraInfo().forEach(s -> {
                line.append(" ");
                line.append(s);
            });
            Optional.ofNullable(recordValue.getFilter()).ifPresent(filter -> {
                line.append(" ");
                line.append(Y_FILTER);
                line.append(" ");
                line.append(filter);
            });

            if (!aRecords.containsKey(k)) {
                aRecords.put(k, new LinkedList<>());
            }
            aRecords.get(k).add(line.toString());
        }
        result.put(Y_ACCESS, aRecords);

        List<String> triggersList = new LinkedList<>();
        Map<TriggerKey, TriggerValue> triggers = state.getTriggers();
        for (TriggerKey tKey : triggers.keySet()) {
            String key = tKey.toSpacedString();
            TriggerValue tValue = triggers.get(tKey);
            triggersList.add(String.format("%s %s %s %s", key, tValue.getInput(), Y_PROGRAM, tValue.getProgram()));
        }
        if (!triggersList.isEmpty()) {
            result.put(Y_TRIGGERS, triggersList);
        }

        if (state instanceof PolicyState) {
            PolicyState st = (PolicyState) state;
            List<Map<String, Object>> sigMaps = new LinkedList<>();
            List<PolicySignature> sigs = st.getSignatures();
            for (PolicySignature sig : sigs) {
                Map<String, Object> sigMap = new LinkedHashMap<>();
                sigMap.put(Y_NAME, sig.getName());
                Optional.ofNullable(sig.getBranch()).ifPresent(v -> sigMap.put(Y_BRANCH, v));
                Set<String> approve = sig.getApprove();
                sigMap.put(Y_APPROVE, approve);
                Set<String> ignore = sig.getIgnore();
                sigMap.put(Y_IGNORE, ignore);
                Set<String> reject = sig.getReject();
                sigMap.put(Y_REJECT, reject);
                Optional.ofNullable(sig.getFilter()).ifPresent(v -> sigMap.put(Y_FILTER, v));
                sigMaps.add(sigMap);
            }
            if (!sigMaps.isEmpty()) {
                result.put(Y_SIGNATURES, sigMaps);
            }
        }
        return result;
    }
}
