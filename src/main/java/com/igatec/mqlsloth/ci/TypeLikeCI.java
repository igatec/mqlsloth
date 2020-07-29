package com.igatec.mqlsloth.ci;

import com.igatec.mqlsloth.ci.annotation.ModBooleanProvider;
import com.igatec.mqlsloth.ci.annotation.ModReversibleStringProvider;
import com.igatec.mqlsloth.ci.annotation.ModStringSetProvider;
import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.ci.constants.SlothAdminType;
import com.igatec.mqlsloth.util.ReversibleSet;
import com.igatec.mqlsloth.util.SlothSet;
import com.igatec.mqlsloth.util.SlothString;

import java.util.Map;

public abstract class TypeLikeCI extends AdminObjectCI {

    private Boolean isAbstract;
    private SlothString parentType;
    private ReversibleSet<String> attributes;

    public TypeLikeCI(SlothAdminType aType, String name, CIDiffMode diffMode) {
        super(aType, name, diffMode);
        if (getDiffMode() == CIDiffMode.TARGET) {
            initTarget();
        } else {
            initDiff();
        }
    }

    private void initTarget() {
        isAbstract = false;
        parentType = new SlothString();
        attributes = new SlothSet<>(false);
    }

    private void initDiff() {
        isAbstract = null;
        parentType = null;
        attributes = new SlothSet<>(true);
    }


    @ModBooleanProvider(value = M_ABSTRACT, useTrueFalse = true)
    public Boolean isAbstract() {
        return isAbstract;
    }

    public void setAbstract(Boolean anAbstract) {
        checkModeAssertion(anAbstract != null, CIDiffMode.TARGET);
        isAbstract = anAbstract;
    }

    @ModReversibleStringProvider(value = M_DERIVED, setPriority = SP_SET_DERIVED)
    public SlothString getParentType() {
        return parentType;
    }

    public void setParentType(SlothString parentType) {
        checkModeAssertion(parentType != null, CIDiffMode.TARGET);
        this.parentType = parentType;
    }

    public void deleteParentType() {
        setParentType(new SlothString(null));
    }


    @ModStringSetProvider(value = M_ATTRIBUTE, addPriority = SP_ADD_ATTRIBUTE_TO)
    public ReversibleSet<String> getAttributes() {
        return new SlothSet<>(attributes, isDiffMode());
    }

    public boolean addAttribute(String attribute) {
        return attributes.add(attribute);
    }

    public boolean reverseAttribute(String attribute) {
        checkModeAssertion(CIDiffMode.DIFF);
        return attributes.reverse(attribute);
    }

    @Override
    protected void fillDiffCI(AbstractCI newCI, AbstractCI diffCI) {
        super.fillDiffCI(newCI, diffCI);
        TypeLikeCI newCastedCI = (TypeLikeCI) newCI;
        TypeLikeCI diffCastedCI = (TypeLikeCI) diffCI;
        {
            Boolean value = newCastedCI.isAbstract();
            if (value != null && !value.equals(isAbstract())) {
                diffCastedCI.setAbstract(value);
            }
        }
        {
            SlothString value = newCastedCI.getParentType();
            if (value != null && !value.equals(getParentType())) {
                diffCastedCI.setParentType(value);
            }
        }
        {
            ReversibleSet<String> oldValues = getAttributes();
            ReversibleSet<String> newValues = newCastedCI.getAttributes();
            for (String value : SlothSet.itemsToRemove(oldValues, newValues))
                diffCastedCI.reverseAttribute(value);
            for (String value : SlothSet.itemsToAdd(oldValues, newValues))
                diffCastedCI.addAttribute(value);
        }
    }

    public boolean isEmpty() {
        if (!super.isEmpty()) return false;
        return isAbstract == null && parentType == null && attributes.isEmpty();
    }


    public Map<String, Object> toMap() {
        Map<String, Object> fieldsValues = super.toMap();

        fieldsValues.put(Y_ABSTRACT, isAbstract());
        fieldsValues.put(Y_DERIVED, (getParentType() == null) ? null : getParentType().toString());
        fieldsValues.put(Y_ATTRIBUTES, getAttributes());

        return fieldsValues;
    }
}
