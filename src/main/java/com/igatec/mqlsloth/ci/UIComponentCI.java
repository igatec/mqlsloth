package com.igatec.mqlsloth.ci;

import com.igatec.mqlsloth.ci.annotation.ModStringProvider;
import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.ci.constants.ScriptPriority;
import com.igatec.mqlsloth.ci.constants.SlothAdminType;
import com.igatec.mqlsloth.ci.util.CIFullName;
import com.igatec.mqlsloth.script.ModChunk;
import com.igatec.mqlsloth.script.MqlUtil;
import com.igatec.mqlsloth.script.ScriptChunk;
import com.igatec.mqlsloth.util.ReversibleMap;
import com.igatec.mqlsloth.util.SlothDiffMap;
import com.igatec.mqlsloth.util.SlothMapUtil;
import com.igatec.mqlsloth.util.SlothTargetMap;

import java.util.List;
import java.util.Map;

public abstract class UIComponentCI extends AdminObjectCI {

    private String label;
    private String alt;
    private String href;
    private ReversibleMap<String> settings;

    protected UIComponentCI(SlothAdminType aType, String name, CIDiffMode diffMode) {
        super(aType, name, diffMode);
        if (getDiffMode() == CIDiffMode.TARGET) {
            initTarget();
        } else {
            initDiff();
        }
    }

    private void initDiff() {
        label = null;
        alt = null;
        href = null;
        settings = new SlothDiffMap<>();
    }

    private void initTarget() {
        label = "";
        alt = "";
        href = "";
        settings = new SlothTargetMap<>();
    }

    @ModStringProvider(M_LABEL)
    public String getLabel() {
        return label;
    }

    public void setLabel(String value) {
        checkModeAssertion(value != null, CIDiffMode.TARGET);
        this.label = value;
    }

    @ModStringProvider(M_ALT)
    public String getAlt() {
        return alt;
    }

    public void setAlt(String value) {
        checkModeAssertion(value != null, CIDiffMode.TARGET);
        this.alt = value;
    }

    @ModStringProvider(M_HREF)
    public String getHref() {
        return href;
    }

    public void setHref(String value) {
        checkModeAssertion(value != null, CIDiffMode.TARGET);
        this.href = value;
    }

    public void setSetting(String key, String value) {
        checkModeAssertion(value != null, CIDiffMode.TARGET);
        settings.put(key, value);
    }

    public void setSetting(Map.Entry<String, String> setting) {
        String key = setting.getKey();
        String value = setting.getValue();
        setSetting(key, value);
    }

    public void deleteSetting(String property) {
        setSetting(property, null);
    }

    public ReversibleMap<String> getSettings() {
        return settings.clone();
    }

    @Override
    protected void fillDiffCI(AbstractCI newCI, AbstractCI diffCI) {
        super.fillDiffCI(newCI, diffCI);
        UIComponentCI newCastedCI = (UIComponentCI) newCI;
        UIComponentCI diffCastedCI = (UIComponentCI) diffCI;
        String label = newCastedCI.getLabel();
        if (label != null && !label.equals(getLabel())) {
            diffCastedCI.setLabel(label);
        }
        String alt = newCastedCI.getAlt();
        if (alt != null && !alt.equals(getAlt())) {
            diffCastedCI.setAlt(alt);
        }
        String href = newCastedCI.getHref();
        if (href != null && !href.equals(getHref())) {
            diffCastedCI.setHref(href);
        }
        ReversibleMap<String> oldSettings = getSettings();
        ReversibleMap<String> newSettings = newCastedCI.getSettings();
        for (String key : SlothMapUtil.keysToRemove(oldSettings, newSettings)) {
            diffCastedCI.setSetting(key, null);
        }
        for (Map.Entry<String, String> entry : SlothMapUtil.mapToAdd(oldSettings, newSettings).entrySet()) {
            diffCastedCI.setSetting(entry.getKey(), entry.getValue());
        }
    }

    public boolean isEmpty() {
        if (!super.isEmpty()) {
            return false;
        }
        return label == null && alt == null && href == null && settings.isEmpty();
    }

    @Override
    public List<ScriptChunk> buildUpdateScript() {
        List<ScriptChunk> chunks = super.buildUpdateScript();
        CIFullName fName = getCIFullName();
        SlothDiffMap<String> settings = (SlothDiffMap) getSettings();
        for (String key : settings.keysToRemove()) {
            chunks.add(new ModChunk(fName, ScriptPriority.SP_AFTER_ADMIN_CREATION_1, M_REMOVE, M_SETTING, MqlUtil.qWrap(key)));
        }
        Map<String, String> mapToAdd = settings.mapToAdd();
        for (String key : mapToAdd.keySet()) {
            chunks.add(
                    new ModChunk(
                            fName,
                            ScriptPriority.SP_AFTER_ADMIN_CREATION_1, M_ADD, M_SETTING, MqlUtil.qWrap(key),
                            MqlUtil.qWrap(mapToAdd.get(key))
                    )
            );
        }
        return chunks;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> fieldsValues = super.toMap();
        fieldsValues.put(Y_LABEL, getLabel());
        fieldsValues.put(Y_ALT, getAlt());
        fieldsValues.put(Y_HREF, getHref());
        fieldsValues.put(Y_SETTINGS, getSettings());
        return fieldsValues;
    }
}
