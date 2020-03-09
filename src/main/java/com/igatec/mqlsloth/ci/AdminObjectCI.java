package com.igatec.mqlsloth.ci;

import com.igatec.mqlsloth.ci.annotation.ModBooleanProvider;
import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.ci.constants.SlothAdminType;
import com.igatec.mqlsloth.ci.util.CIFullName;
import com.igatec.mqlsloth.ci.util.StringCIName;
import com.igatec.mqlsloth.script.ModChunk;
import com.igatec.mqlsloth.script.ModSymbolicNameChunk;
import com.igatec.mqlsloth.script.MqlUtil;
import com.igatec.mqlsloth.script.ScriptChunk;
import com.igatec.mqlsloth.util.ReversibleMap;
import com.igatec.mqlsloth.util.SlothDiffMap;
import com.igatec.mqlsloth.util.SlothMapUtil;
import com.igatec.mqlsloth.util.SlothTargetMap;

import java.util.List;
import java.util.Map;

public abstract class AdminObjectCI extends AbstractCI {

    private ReversibleMap<String> properties;
    private Boolean hidden;
    private String symbolicName;

    protected AdminObjectCI(SlothAdminType aType, String name, CIDiffMode diffMode) {
        super(aType, new StringCIName(name), diffMode);
        if (getDiffMode() == CIDiffMode.TARGET) {
            initTarget();
        } else {
            initDiff();
        }
    }

    private void initDiff() {
        properties = new SlothDiffMap<>();
        hidden = null;
        symbolicName = null;
    }

    private void initTarget() {
        properties = new SlothTargetMap<>();
        hidden = false;
        symbolicName = "";
    }


    public String getName() {
        return getCIName().toString();
    }

    public void setSymbolicName(String value) {
        symbolicName = value;
    }

    public String getSymbolicName() {
        return symbolicName;
    }

    public void setProperty(String key, String value) {
        checkModeAssertion(value != null, CIDiffMode.TARGET);
        properties.put(key, value);
    }

    public void setProperty(Map.Entry<String, String> property) {
        String key = property.getKey();
        String value = property.getValue();
        setProperty(key, value);
    }

    public void deleteProperty(String property) {
        setProperty(property, null);
    }

    public String getProperty(String key) {
        return properties.get(key);
    }

    public ReversibleMap<String> getProperties() {
        return properties.clone();
    }

    public boolean hasProperty(String key) {
        return properties.containsKey(key);
    }


    @ModBooleanProvider(M_HIDDEN)
    public Boolean isHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        checkModeAssertion(hidden != null, CIDiffMode.TARGET);
        this.hidden = hidden;
    }

    @Override
    protected void fillDiffCI(AbstractCI newCI, AbstractCI diffCI) {
        super.fillDiffCI(newCI, diffCI);
        AdminObjectCI newCastedCI = (AdminObjectCI) newCI;
        AdminObjectCI diffCastedCI = (AdminObjectCI) diffCI;
        {
            Boolean value = newCastedCI.isHidden();
            if (value != null && !value.equals(isHidden())) {
                diffCastedCI.setHidden(value);
            }
        }
        {
            String value = newCastedCI.getSymbolicName();
            if (value != null && !value.equals(getSymbolicName())) {
                diffCastedCI.setSymbolicName(value);
            }
        }
        {
            ReversibleMap<String> oldProps = getProperties();
            ReversibleMap<String> newProps = newCastedCI.getProperties();
            for (String key : SlothMapUtil.keysToRemove(oldProps, newProps))
                diffCastedCI.setProperty(key, null);
            for (Map.Entry<String, String> entry : SlothMapUtil.mapToAdd(oldProps, newProps).entrySet())
                diffCastedCI.setProperty(entry.getKey(), entry.getValue());
        }
    }

    public boolean isEmpty() {
        if (!super.isEmpty()) return false;
        return hidden == null && properties.isEmpty();
    }


    @Override
    public List<ScriptChunk> buildUpdateScript() {
        List<ScriptChunk> chunks = super.buildUpdateScript();
        CIFullName fName = getCIFullName();

        /* PROPERTIES */
        SlothDiffMap<String> props = (SlothDiffMap) getProperties();
        for (String key : props.keysToRemove())
            chunks.add(new ModChunk(fName, M_REMOVE, M_PROPERTY, MqlUtil.qWrap(key)));
        Map<String, String> mapToAdd = props.mapToAdd();
        for (String key : mapToAdd.keySet())
            chunks.add(new ModChunk(fName, M_PROPERTY, MqlUtil.qWrap(key), M_VALUE, MqlUtil.qWrap(mapToAdd.get(key))));

        /* SYMBOLIC NAME */
        String symbolicName = getSymbolicName();
        if (symbolicName != null) {
            chunks.add(new ModSymbolicNameChunk(fName, symbolicName));
        }

        return chunks;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> fieldsValues = super.toMap();

        fieldsValues.put(Y_SYMBOLIC_NAME, getSymbolicName());
        fieldsValues.put(Y_PROPERTIES, getProperties());
        fieldsValues.put(Y_HIDDEN, isHidden());

        return fieldsValues;
    }

    public void setDefaultSymbolicName() {
        String type = getSlothAdminType().toString();
        String name = getCIName().toString();
        String symbolicName = type.toLowerCase() + "_" + name.replaceAll(" ", "");
        setSymbolicName(symbolicName);
    }
}
