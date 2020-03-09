package com.igatec.mqlsloth.ci;

import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.ci.constants.SlothAdminType;
import com.igatec.mqlsloth.ci.util.BusCIName;
import com.igatec.mqlsloth.ci.util.CIFullName;
import com.igatec.mqlsloth.script.ScriptChunk;
import com.igatec.mqlsloth.util.ReversibleMap;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class NumberGeneratorCI extends AdminBusCI {

    public final static String BUS_TYPE = M_NUMBER_GENERATOR;
    public final static String ATTR_NEXT_NUMBER = "eService Next Number";


    public static CIFullName createCIFullName(String name, String revision){
        return new CIFullName(SlothAdminType.NUMBER_GENERATOR, new BusCIName(BUS_TYPE, name, revision));
    }

    private String nextNumber;

    public NumberGeneratorCI(String name, String revision){
        this(name, revision, CIDiffMode.TARGET);
    }
    public NumberGeneratorCI(String name, String revision, String vault){
        this(name, revision, CIDiffMode.TARGET);
    }
    public NumberGeneratorCI(String name, String revision, CIDiffMode diffMode) {
        super(
                SlothAdminType.NUMBER_GENERATOR,
                new BusCIName(BUS_TYPE, name, revision),
                OBJECT_GENERATOR_POLICY, diffMode
        );
        if (getDiffMode() == CIDiffMode.TARGET) {
            initTarget();
        } else {
            initDiff();
        }
    }
    private void initDiff(){
        nextNumber = null;
    }
    private void initTarget(){
        nextNumber = "";
    }


    public String getNextNumber(){
        return nextNumber;
    }
    public void setNextNumber(String value){
        checkModeAssertion(value != null, CIDiffMode.TARGET);
        nextNumber = value;
    }

    @Override
    public void setAttribute(String key, String value){
//        checkCIConstraint(ATTR_NEXT_NUMBER.equals(key), "Number generator object does not contain attribute '"+key+"'");
        super.setAttribute(key, value);
    }

    @Override
    public NumberGeneratorCI buildDiff(AbstractCI newCI){
        BusCIName tnr = (BusCIName) getCIName();
        NumberGeneratorCI ci = (NumberGeneratorCI) newCI;
        NumberGeneratorCI diff = new NumberGeneratorCI(tnr.getName(), tnr.getRevision(), CIDiffMode.DIFF);
        fillDiffCI(ci, diff);
        return diff;
    }

    @Override
    public AbstractCI buildDefaultCI(){
        BusCIName tnr = (BusCIName) getCIName();
        return new NumberGeneratorCI(tnr.getName(), tnr.getRevision());
    }

}
