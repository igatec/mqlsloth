package com.igatec.mqlsloth.ci;

import com.igatec.mqlsloth.ci.annotation.ModStringProvider;
import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.ci.constants.SlothAdminType;

import java.util.Map;

public abstract class UserCombinationCI extends AdminObjectCI {

    private String parent;

    protected UserCombinationCI(SlothAdminType aType, String name, CIDiffMode diffMode) {
        super(aType, name, diffMode);
        if (getDiffMode() == CIDiffMode.TARGET) {
            initTarget();
        } else {
            initDiff();
        }
    }

    private void initTarget() {
        parent = "";
    }

    private void initDiff() {
        parent = null;
    }

    @ModStringProvider(value = M_PARENT, useRemove = true, setPriority = SP_AFTER_ADMIN_CREATION_1)
    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        checkModeAssertion(parent != null, CIDiffMode.TARGET);
        this.parent = parent;
    }

    @Override
    protected void fillDiffCI(AbstractCI newCI, AbstractCI diffCI) {
        super.fillDiffCI(newCI, diffCI);
        UserCombinationCI newCastedCI = (UserCombinationCI) newCI;
        UserCombinationCI diffCastedCI = (UserCombinationCI) diffCI;
        String parent = newCastedCI.getParent();
        if (parent != null && !parent.equals(getParent())) {
            diffCastedCI.setParent(parent);
        }
    }

    @Override
    public boolean isEmpty() {
        if (!super.isEmpty()) {
            return false;
        }
        return parent == null;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> fieldsValues = super.toMap();
        fieldsValues.put(Y_PARENT, getParent());
        return fieldsValues;
    }
}
