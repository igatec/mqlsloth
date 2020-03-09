package com.igatec.mqlsloth.ci;

import com.igatec.mqlsloth.ci.annotation.ModBooleanProvider;
import com.igatec.mqlsloth.ci.annotation.Unmodifiable;
import com.igatec.mqlsloth.ci.constants.*;
import java.util.*;

public class TypeCI extends TypeLikeCI {

    private Boolean isComposed;

    public TypeCI(String name) {
        this(name, CIDiffMode.TARGET);
    }
    public TypeCI(String name, CIDiffMode diffMode) {
        super(SlothAdminType.TYPE, name, diffMode);
        if (getDiffMode() == CIDiffMode.TARGET) {
            initTarget();
        } else {
            initDiff();
        }
    }
    private void initTarget(){
        isComposed = false;
    }
    private void initDiff(){
        isComposed = null;
    }

    @Unmodifiable(message = "Field '" + M_COMPOSED + "' cannot be modified")
    @ModBooleanProvider(M_COMPOSED)
    public Boolean isComposed() {
        return isComposed;
    }
    public void setComposed(Boolean composed) {
        checkModeAssertion(composed != null, CIDiffMode.TARGET);
        isComposed = composed;
    }

    @Override
    protected void fillDiffCI(AbstractCI newCI, AbstractCI diffCI){
        super.fillDiffCI(newCI, diffCI);
        TypeCI newCastedCI = (TypeCI) newCI;
        TypeCI diffCastedCI = (TypeCI) diffCI;
        {
            Boolean value = newCastedCI.isComposed();
            if (value != null && !value.equals(isComposed())){
                diffCastedCI.setComposed(value);
            }
        }
    }

    @Override
    public TypeCI buildDiff(AbstractCI newCI){
        TypeCI ci = (TypeCI) newCI;
        TypeCI diff = new TypeCI(getName(), CIDiffMode.DIFF);
        fillDiffCI(ci, diff);
        return diff;
    }

    public boolean isEmpty(){
        if (!super.isEmpty()) return false;
        return isComposed == null;
    }

    @Override
    public AbstractCI buildDefaultCI(){
        return new TypeCI(getName());
    }


    public Map<String, Object> toMap(){
        Map<String, Object> fieldsValues = super.toMap();
        fieldsValues.put(Y_COMPOSED, isComposed());

        return fieldsValues;
    }
}
