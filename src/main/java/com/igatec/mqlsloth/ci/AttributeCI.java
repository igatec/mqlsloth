package com.igatec.mqlsloth.ci;

import com.igatec.mqlsloth.ci.annotation.ModBooleanProvider;
import com.igatec.mqlsloth.ci.annotation.ModStringProvider;
import com.igatec.mqlsloth.ci.annotation.ModStringSetProvider;
import com.igatec.mqlsloth.ci.constants.AttributeType;
import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.ci.constants.SlothAdminType;
import com.igatec.mqlsloth.script.AttributeCreateChunk;
import com.igatec.mqlsloth.script.ScriptChunk;
import com.igatec.mqlsloth.util.ReversibleSet;
import com.igatec.mqlsloth.util.ReversibleString;
import com.igatec.mqlsloth.util.SlothSet;
import com.igatec.mqlsloth.util.SlothString;

import java.util.Map;

public class AttributeCI extends AdminObjectCI {
    private final AttributeType type;
    private Boolean isMultivalue;
    private Boolean isMultiline;
    private ReversibleString defaultValue;
    private ReversibleSet<String> range;
    private Integer maxLength;
    private Boolean resetOnClone;
    private Boolean resetOnRevision;

    public AttributeCI(String name, AttributeType type) {
        this(name, type, CIDiffMode.TARGET);
    }

    public AttributeCI(String name, AttributeType type, CIDiffMode diffMode) {
        super(SlothAdminType.ATTRIBUTE, name, diffMode);
        this.type = type;
        if (getDiffMode() == CIDiffMode.TARGET) {
            initTarget();
        } else {
            initDiff();
        }
    }

    private void initTarget() {
        isMultivalue = false;
        isMultiline = false;
        defaultValue = new SlothString();
        range = new SlothSet<>(false);
        maxLength = 0;
        resetOnClone = false;
        resetOnRevision = false;
    }

    private void initDiff() {
        isMultivalue = null;
        isMultiline = null;
        defaultValue = null;
        range = new SlothSet<>(true);
        maxLength = null;
        resetOnClone = null;
        resetOnRevision = null;
    }

    public AttributeType getType() {
        return type;
    }

    @ModBooleanProvider(
            value = M_MULTIVALUE,
            setFalsePriority = SP_SET_ATTR_NOT_MULTIVALUE,
            setTruePriority = SP_SET_ATTR_MULTIVALUE
    )
    public Boolean isMultivalue() {
        return isMultivalue;
    }

    public void setMultivalue(Boolean multivalue) {
        checkModeAssertion(multivalue != null, CIDiffMode.TARGET);
        isMultivalue = multivalue;
    }

    @ModBooleanProvider(M_MULTILINE)
    public Boolean isMultiline() {
        return isMultiline;
    }

    public void setMultiline(Boolean multiline) {
        checkModeAssertion(multiline != null, CIDiffMode.TARGET);
        isMultiline = multiline;
    }

    @ModStringProvider(value = M_DEFAULT, setPriority = SP_SET_ATTR_DEFAULT, removePriority = SP_REM_ATTR_DEFAULT)
    public ReversibleString getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(ReversibleString defaultValue) {
        checkModeAssertion(defaultValue != null, CIDiffMode.TARGET);
        this.defaultValue = defaultValue;
    }

    public void deleteDefaultValue() {
        setDefaultValue(new SlothString(null));
    }

    @ModStringSetProvider(value = M_RANGE, valueAppend = "=")
    public ReversibleSet<String> getRange() {
        return new SlothSet<>(range, isDiffMode());
    }

    public Boolean addRange(String rangeItem) {
        return range.add(rangeItem);
    }

    public boolean reverseRange(String rangeItem) {
        checkModeAssertion(CIDiffMode.DIFF);
        return range.reverse(rangeItem);
    }

    @ModStringProvider(M_MAX_LENGTH)
    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        checkModeAssertion(maxLength != null, CIDiffMode.TARGET);
        this.maxLength = maxLength;
    }

    @ModBooleanProvider(M_RESET_ON_CLONE)
    public Boolean isResetOnClone() {
        return resetOnClone;
    }

    public void setResetOnClone(Boolean resetOnClone) {
        checkModeAssertion(resetOnClone != null, CIDiffMode.TARGET);
        this.resetOnClone = resetOnClone;
    }

    @ModBooleanProvider(M_RESET_ON_REVISION)
    public Boolean isResetOnRevision() {
        return resetOnRevision;
    }

    public void setResetOnRevision(Boolean resetOnRevision) {
        checkModeAssertion(resetOnRevision != null, CIDiffMode.TARGET);
        this.resetOnRevision = resetOnRevision;
    }

    @Override
    protected void fillDiffCI(AbstractCI newCI, AbstractCI diffCI) {
        super.fillDiffCI(newCI, diffCI);
        AttributeCI newCastedCI = (AttributeCI) newCI;
        AttributeCI diffCastedCI = (AttributeCI) diffCI;
        checkCIConstraint("Attribute type conflict", getType() == newCastedCI.getType());
        Boolean isMultiValue = newCastedCI.isMultivalue();
        if (isMultiValue != null && !isMultiValue.equals(isMultivalue())) {
            diffCastedCI.setMultivalue(isMultiValue);
        }
        Boolean isMultiline = newCastedCI.isMultiline();
        if (isMultiline != null && !isMultiline.equals(isMultiline())) {
            diffCastedCI.setMultiline(isMultiline);
        }
        Boolean isResetOnClone = newCastedCI.isResetOnClone();
        if (isResetOnClone != null && !isResetOnClone.equals(isResetOnClone())) {
            diffCastedCI.setResetOnClone(isResetOnClone);
        }
        Boolean isResetOnRevision = newCastedCI.isResetOnRevision();
        if (isResetOnRevision != null && !isResetOnRevision.equals(isResetOnRevision())) {
            diffCastedCI.setResetOnRevision(isResetOnRevision);
        }
        Integer maxLength = newCastedCI.getMaxLength();
        if (maxLength != null && !maxLength.equals(getMaxLength())) {
            diffCastedCI.setMaxLength(maxLength);
        }
        ReversibleString defaultValue = newCastedCI.getDefaultValue();
        if (defaultValue != null && !defaultValue.equals(getDefaultValue())) {
            diffCastedCI.setDefaultValue(defaultValue);
        }
        ReversibleSet<String> oldValues = getRange();
        ReversibleSet<String> newValues = newCastedCI.getRange();
        for (String value : SlothSet.itemsToRemove(oldValues, newValues)) {
            diffCastedCI.reverseRange(value);
        }
        for (String value : SlothSet.itemsToAdd(oldValues, newValues)) {
            diffCastedCI.addRange(value);
        }
    }

    @Override
    public boolean isEmpty() {
        if (!super.isEmpty()) {
            return false;
        }
        return isMultiline == null
                && isMultivalue == null
                && resetOnClone == null
                && resetOnRevision == null
                && maxLength == null
                && range.isEmpty()
                && defaultValue == null;
    }

    @Override
    public AttributeCI buildDiff(AbstractCI newCI) {
        AttributeCI ci = (AttributeCI) newCI;
        AttributeCI diff = new AttributeCI(getName(), getType(), CIDiffMode.DIFF);
        fillDiffCI(ci, diff);
        return diff;
    }

    @Override
    protected ScriptChunk buildCreateChunk() {
        return new AttributeCreateChunk(getCIFullName(), getType());
    }

    @Override
    public AbstractCI buildDefaultCI() {
        return new AttributeCI(getName(), getType());
    }

    public Map<String, Object> toMap() {
        Map<String, Object> fieldsValues = super.toMap();

        fieldsValues.put(Y_TYPE, getType());
        fieldsValues.put(Y_MULTIVALUE, isMultivalue());
        fieldsValues.put(Y_MULTILINE, isMultiline());
        fieldsValues.put(Y_DEFAULT, (getDefaultValue() == null) ? null : getDefaultValue().toString());
        fieldsValues.put(Y_RANGES, getRange());
        fieldsValues.put(Y_RESET_ON_CLONE, isResetOnClone());
        fieldsValues.put(Y_RESET_ON_REVISION, isResetOnRevision());

        return fieldsValues;
    }

}
