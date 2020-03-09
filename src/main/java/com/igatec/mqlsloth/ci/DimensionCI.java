package com.igatec.mqlsloth.ci;

import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.ci.constants.SlothAdminType;
import com.igatec.mqlsloth.ci.util.CIFullName;
import com.igatec.mqlsloth.script.CreateChunk;
import com.igatec.mqlsloth.script.DeleteChunk;
import com.igatec.mqlsloth.script.ScriptChunk;
import com.igatec.mqlsloth.script.assertion.NoBusinessDataOfType;
import com.igatec.mqlsloth.util.ReversibleMap;
import com.igatec.mqlsloth.util.SlothDiffMap;
import com.igatec.mqlsloth.util.SlothTargetMap;

import java.util.*;

public class DimensionCI extends AdminObjectCI {

    private ReversibleMap<DimCIUnit> units;

    public DimensionCI(String name) {
        this(name, CIDiffMode.TARGET);
    }
    public DimensionCI(String name, CIDiffMode diffMode) {
        super(null, name, diffMode);
//        super(SlothAdminType.DIMENSION, name, diffMode); // todo
        if (getDiffMode() == CIDiffMode.TARGET) {
            initTarget();
        } else {
            initDiff();
        }
    }

    private void initTarget(){
        units = new SlothTargetMap<>();
    }

    private void initDiff(){
        units = new SlothDiffMap<>();
    }

    public void setPropertiy(String unit, String key, String value){
        checkModeAssertion(value != null, CIDiffMode.TARGET);
        units.get(unit).properties.put(key, value);
    }
    public String getPropertiy(String unit, String key){
        return units.get(unit).properties.get(key);
    }
    public ReversibleMap<String> getProperties(String unit) {
        return units.get(unit).properties.clone();
    }
    public boolean hasProperty(String unit, String key){
        return units.get(unit).properties.containsKey(key);
    }

    public void addUnit(String name){
        if (units.containsKey(name))
            throw new CIConstraintException("Dimendion " + getName() + " already has unit " + name);
        units.put(name, new DimCIUnit(name, isDiffMode()));
    }

    public Set<String> getUnits(){
        return units.keySet();
    }

    public void setUnitLabel(String name, String label){
        units.get(name).label = label;
    }
    public String getUnitLabel(String name){
        return units.get(name).label;
    }

    public void setUnitMultiplier(String name, Double multiplier){
        units.get(name).multiplier = multiplier;
    }
    public Double getUnitMultiplier(String name){
        return units.get(name).multiplier;
    }

    public void setUnitOffset(String name, Double offset){
        units.get(name).offset = offset;
    }
    public Double getUnitOffset(String name){
        return units.get(name).offset;
    }



    @Override
    public DimensionCI buildDiff(AbstractCI newCI) {
        return null;
    }

    @Override
    public AbstractCI buildDefaultCI() {
        return null;
    }


    private static class DimCIUnit {

        private final String name;
        private String label;
        private Double multiplier;
        private Double offset;
        private ReversibleMap<String> properties;

        DimCIUnit(String name, boolean diffMode){
            this.name = name;
            if (diffMode)
                initDiff();
            else
                initTarget();
        }

        private void initDiff(){
            label = null;
            multiplier = null;
            offset = null;
            properties = new SlothTargetMap<>();
        }

        private void initTarget(){
            label = "";
            multiplier = 1.0;
            offset = 0.0;
            properties = new SlothDiffMap<>();
        }

    }


}





