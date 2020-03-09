package com.igatec.mqlsloth.ci;

import com.igatec.mqlsloth.ci.annotation.ModStringProvider;
import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.ci.constants.SlothAdminType;

import java.util.Map;

public class ExpressionCI extends AdminObjectCI {
    private String value;

    public ExpressionCI(String name, CIDiffMode diffMode) {
        super(SlothAdminType.EXPRESSION, name, diffMode);
        if (getDiffMode() == CIDiffMode.TARGET) {
            initTarget();
        } else {
            initDiff();
        }
    }

    public ExpressionCI(String name) {
        this(name, CIDiffMode.TARGET);
    }

    @ModStringProvider(M_VALUE)
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        checkModeAssertion(value != null, CIDiffMode.TARGET);
        this.value = value;
    }

    private void initTarget() {
        value = "";
    }

    @Override
    public boolean isEmpty() {
        if (!super.isEmpty()) {
            return false;
        }
        return value == null;
    }

    private void initDiff() {
        value = null;
    }

    @Override
    protected void fillDiffCI(AbstractCI newCI, AbstractCI diffCI) {
        super.fillDiffCI(newCI, diffCI);
        ExpressionCI newCastedCI = (ExpressionCI) newCI;
        ExpressionCI diffCastedCI = (ExpressionCI) diffCI;

        String val = newCastedCI.getValue();
        if (val != null && !val.equals(getValue())) {
            diffCastedCI.setValue(val);
        }
    }

    @Override
    public AbstractCI buildDiff(AbstractCI newCI) {
        ExpressionCI ci = (ExpressionCI) newCI;
        ExpressionCI diff = new ExpressionCI(getName(), CIDiffMode.DIFF);
        fillDiffCI(ci, diff);
        return diff;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> fieldsValues = super.toMap();
        fieldsValues.put(M_VALUE, getValue());
        return fieldsValues;
    }

    @Override
    public AbstractCI buildDefaultCI() {
        return new ExpressionCI(getName());
    }
}
