package com.igatec.mqlsloth.ci;

import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.ci.constants.SlothAdminType;
import com.igatec.mqlsloth.ci.util.BusCIName;
import com.igatec.mqlsloth.script.ConnectBusChunk;
import com.igatec.mqlsloth.script.CreateBusChunk;
import com.igatec.mqlsloth.script.ModBusChunk;
import com.igatec.mqlsloth.script.ScriptChunk;
import com.igatec.mqlsloth.util.ReversibleSet;
import com.igatec.mqlsloth.util.SlothSet;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractBusCI extends AbstractCI {

    private final String vault;
    private Map<String, String> attributes;
    private String policy;
    private String state;
    private ReversibleSet<ConnectionPointer> fromConnections;

    protected AbstractBusCI(SlothAdminType aType, BusCIName ciName, String vault, String policy, CIDiffMode diffMode) {
        super(aType, ciName, diffMode);
        this.vault = vault;
        if (getDiffMode() == CIDiffMode.TARGET) {
            initTarget();
        } else {
            initDiff();
        }
        setPolicy(policy);
    }

    private void initDiff() {
        attributes = new HashMap<>();
        state = null;
        fromConnections = new SlothSet<>(true);
    }

    private void initTarget() {
        attributes = new HashMap<>();
        state = "";
        fromConnections = new SlothSet<>(false);
    }

    public String getVault() {
        return vault;
    }

    //    @Unmodifiable(message = "Bus policy cannot be changed")
//    @ModStringProvider(M_POLICY)
    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        checkModeAssertion(policy != null, CIDiffMode.TARGET);
        this.policy = policy;
    }

    //    @Unmodifiable(message = "Bus state cannot be changed")
//    @ModStringProvider(M_CURRENT)
    public String getState() {
        return state;
    }

    public void setState(String state) {
        checkModeAssertion(state != null, CIDiffMode.TARGET);
        this.state = state;
    }

    public void setAttribute(Map.Entry<String, String> attribute) {
        String key = attribute.getKey();
        String value = attribute.getValue();
        setAttribute(key, value);
    }

    public Map<String, String> getAttributes() {
        return new HashMap<>(attributes);
    }

    public String getAttribute(String key) {
        return attributes.get(key);
    }

    public void setAttribute(String key, String value) {
        checkModeAssertion(value != null, CIDiffMode.TARGET);
        attributes.put(key, value);
    }

    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }

    public ReversibleSet<ConnectionPointer> getFromConnections() {
        return new SlothSet<>(fromConnections, isDiffMode());
    }

    public Boolean addFromConnection(ConnectionPointer connection) {
        return fromConnections.add(connection);
    }

    public boolean reverseFromConnection(ConnectionPointer connection) {
        checkModeAssertion(CIDiffMode.DIFF);
        return fromConnections.reverse(connection);
    }

    @Override
    protected void fillDiffCI(AbstractCI newCI, AbstractCI diffCI) {
        super.fillDiffCI(newCI, diffCI);
        AbstractBusCI newCastedCI = (AbstractBusCI) newCI;
        AbstractBusCI diffCastedCI = (AbstractBusCI) diffCI;
        checkCIConstraint("Vault conflict", getVault().equals(newCastedCI.getVault()));
        String policy = newCastedCI.getPolicy();
        if (policy != null && !policy.equals(getPolicy())) {
            diffCastedCI.setPolicy(policy);
        }
        String state = newCastedCI.getState();
        if (state != null && !state.equals(getState())) {
            diffCastedCI.setState(state);
        }
        for (Map.Entry<String, String> e : newCastedCI.getAttributes().entrySet()) {
            String k = e.getKey();
            String v = e.getValue();
            String oldV = getAttribute(k);
            if (!v.equals(oldV)) {
                if (!NumberGeneratorCI.ATTR_NEXT_NUMBER.equals(k) || (oldV == null || oldV.isEmpty())) {
                    diffCastedCI.setAttribute(k, v);
                }
            }
        }
        ReversibleSet<ConnectionPointer> oldValues = getFromConnections();
        ReversibleSet<ConnectionPointer> newValues = newCastedCI.getFromConnections();
        for (ConnectionPointer value : SlothSet.itemsToRemove(oldValues, newValues)) {
            diffCastedCI.reverseFromConnection(value);
        }
        for (ConnectionPointer value : SlothSet.itemsToAdd(oldValues, newValues)) {
            diffCastedCI.addFromConnection(value);
        }
    }

    @Override
    public boolean isEmpty() {
        if (!super.isEmpty()) {
            return false;
        }
        for (String val : attributes.values()) {
            if (val != null) {
                return false;
            }
        }
        return /*policy==null && state==null*/ true;
    }

    @Override
    protected ScriptChunk buildCreateChunk() {
        return new CreateBusChunk(getCIFullName(), getPolicy(), getVault());
    }

    public List<ScriptChunk> buildUpdateScript() {
        List<ScriptChunk> result = super.buildUpdateScript();

        /* ATTRIBUTES */
        for (String attr : getAttributes().keySet()) {
            String value = getAttribute(attr);
            if (value != null) {
                result.add(new ModBusChunk(getCIFullName(), attr, value));
            }
        }

        /* CONNECTIONS */
        BusCIName thisName = (BusCIName) getCIName();
        ReversibleSet<ConnectionPointer> connections = getFromConnections();
        for (ConnectionPointer toDisconnect : connections.getReversed()) {
            result.add(new ConnectBusChunk(thisName, toDisconnect.getRel(), toDisconnect.getTo(), false));
        }
        for (ConnectionPointer toDisconnect : connections.get()) {
            result.add(new ConnectBusChunk(thisName, toDisconnect.getRel(), toDisconnect.getTo(), true));
        }

        return result;
    }

    public static class ConnectionPointer {
        private final String relName;
        private final BusCIName toTNR;

        public ConnectionPointer(String relName, BusCIName toBus) {
            this.relName = relName;
            this.toTNR = toBus;
        }

        public String getRel() {
            return relName;
        }

        public BusCIName getTo() {
            return toTNR;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ConnectionPointer that = (ConnectionPointer) o;
            return Objects.equals(relName, that.relName)
                    && Objects.equals(toTNR, that.toTNR);
        }

        @Override
        public int hashCode() {
            return Objects.hash(relName, toTNR);
        }
    }

    public Map<String, Object> toMap() {
        Map<String, Object> fieldsValues = super.toMap();

        fieldsValues.put(Y_POLICY, getPolicy());
        fieldsValues.put(Y_STATE, getState());
        fieldsValues.put(Y_VAULT, getVault());
        fieldsValues.put(Y_ATTRIBUTES, getAttributes());

        List<Map<String, String>> fromConnections = new LinkedList<>();
        for (ConnectionPointer cp : getFromConnections()) {
            Map<String, String> connectionMap = new LinkedHashMap<>();
            connectionMap.put(Y_REL, cp.getRel());
            BusCIName busName = cp.getTo();
            connectionMap.put(Y_TYPE, busName.getType());
            connectionMap.put(Y_NAME, busName.getName());
            connectionMap.put(Y_REVISION, busName.getRevision());
            fromConnections.add(connectionMap);
        }
        fieldsValues.put(Y_FROM, fromConnections);

        return fieldsValues;
    }
}
