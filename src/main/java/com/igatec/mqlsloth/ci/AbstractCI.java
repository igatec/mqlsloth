package com.igatec.mqlsloth.ci;

import com.igatec.mqlsloth.ci.annotation.*;
import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.ci.constants.ScriptPriority;
import com.igatec.mqlsloth.ci.constants.SlothAdminType;
import com.igatec.mqlsloth.ci.util.AbstractCIName;
import com.igatec.mqlsloth.ci.util.BusCIName;
import com.igatec.mqlsloth.ci.util.CIFullName;
import com.igatec.mqlsloth.ci.util.StringCIName;
import com.igatec.mqlsloth.parser.Parser;
import com.igatec.mqlsloth.parser.yaml.YAMLKeysComparator;
import com.igatec.mqlsloth.script.*;
import com.igatec.mqlsloth.util.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public abstract class AbstractCI implements MqlKeywords, YAMLKeywords, ScriptPriority {

    protected final static String MODE_VALIDATION_ERROR = "Mode validation error";

    private final CIDiffMode diffMode;
    private final SlothAdminType aType;
    private final AbstractCIName ciName;
    private String description;

    protected AbstractCI(SlothAdminType aType, AbstractCIName ciName, CIDiffMode diffMode){
        this.diffMode = diffMode;
        this.aType = aType;
        this.ciName = ciName;
        if (getDiffMode() == CIDiffMode.TARGET) {
            initTarget();
        } else {
            initDiff();
        }
    }
    private void initDiff(){
        description = null;
    }
    private void initTarget(){
        description = "";
    }


    @ModStringProvider(M_DESCRIPTION)
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        checkModeAssertion(description != null, CIDiffMode.TARGET);
        this.description = description;
    }


    public CIDiffMode getDiffMode(){
        return diffMode;
    }

    public SlothAdminType getSlothAdminType(){
        return aType;
    }

    public AbstractCIName getCIName(){
        return ciName;
    }

    public CIFullName getCIFullName(){
        return new CIFullName(aType, ciName);
    }

    protected void fillDiffCI(AbstractCI newCI, AbstractCI diffCI){
        CIDiffMode oldDiffMode = getDiffMode();
        CIDiffMode newDiffMode = newCI.getDiffMode();
        checkCIConstraint(oldDiffMode != CIDiffMode.DIFF);
        assertTypeAndNameEquality(newCI);

        {
            String value = newCI.getDescription();
            if (value != null && !value.equals(getDescription())){
                diffCI.setDescription(value);
            }
        }

    }

    public boolean isEmpty(){
        return isDiffMode() && description == null;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (getDiffMode() != ((AbstractCI)o).getDiffMode()) return false;
        try {
            return buildDiff((AbstractCI) o).isEmpty();
        } catch (CIException e){
            return false;
        }
    }


    public abstract AbstractCI buildDiff(AbstractCI newCI);

    public List<ScriptChunk> buildUpdateScript(){
        validateMode(CIDiffMode.DIFF);
        CIFullName fName = getCIFullName();
        List<ScriptChunk> chunks = new LinkedList<>();

        try {
            for (Method method : getClass().getMethods()) {

                {
                    ModBooleanProvider annot = method.getAnnotation(ModBooleanProvider.class);
                    if (annot != null) {
                        Object val = method.invoke(this);
                        if (val != null) {
                            assertIsModifiable(method);
                            String[] param;
                            int priority = (boolean) val ? annot.setTruePriority() : annot.setFalsePriority();
                            if (annot.useTrueFalse()) {
                                String p = (boolean) val ? M_TRUE : M_FALSE;
                                param = sArray(annot.value(), p);
                            } else {
                                String reverse = ("".equals(annot.setFalse())) ? "not" + annot.value() : annot.setFalse();
                                param = sArray( (boolean) val ? annot.value() : reverse );
                            }
                            ScriptChunk chunk = new ModChunk(fName, priority, param);
                            chunks.add(chunk);
                        }
                    }
                }

                {
                    ModStandaloneStringProvider annot = method.getAnnotation(ModStandaloneStringProvider.class);
                    if (annot != null) {
                        Object val = method.invoke(this);
                        if (val != null) {
                            assertIsModifiable(method);
                            ScriptChunk chunk = new ModChunk(fName, annot.priority(), sArray(val));
                            chunks.add(chunk);
                        }
                    }
                }

                {
                    ModStringProvider annot = method.getAnnotation(ModStringProvider.class);
                    if (annot != null) {
                        Object val = method.invoke(this);
                        if (val != null) {
                            assertIsModifiable(method);
                            String[] param;
                            int priority;
                            String sVal = val.toString();
                            if (sVal == null || "".equals(sVal)){
                                priority = annot.removePriority();
                            } else {
                                priority = annot.setPriority();
                            }
                            if (annot.useRemove() && "".equals(val)) {
                                param = sArray(M_REMOVE, annot.value());
                            } else {
                                param = sArray(annot.value(), MqlUtil.qWrap(val));
                            }
                            ScriptChunk chunk = new ModChunk(fName, priority, param);
                            chunks.add(chunk);
                        }
                    }
                }

                {
                    ModReversibleStringProvider annot = method.getAnnotation(ModReversibleStringProvider.class);
                    if (annot != null) {
                        Object val = method.invoke(this);
                        if (val != null) {
                            assertIsModifiable(method);
                            String[] param;
                            String valStr = ((ReversibleString) val).value();
                            int priority;
                            if (valStr == null) {
                                priority = annot.removePriority();
                                param = sArray(M_REMOVE, annot.value());
                            } else {
                                priority = annot.setPriority();
                                param = sArray(annot.value(), MqlUtil.qWrap(valStr));
                            }
                            ScriptChunk chunk = new ModChunk(fName, priority, param);
                            chunks.add(chunk);
                        }
                    }
                }

                {
                    ModStringSetProvider annot = method.getAnnotation(ModStringSetProvider.class);
                    if (annot != null) {
                        boolean modifiable = false;
                        ReversibleSet set = (ReversibleSet) method.invoke(this);
                        Set attrsToRemove = set.getReversed();
                        for (Object attr:attrsToRemove) {
                            if (!modifiable){
                                assertIsModifiable(method);
                                modifiable = true;
                            }
                            chunks.add(new ModChunk(fName, annot.removePriority(), M_REMOVE, annot.value(),
                                    annot.valueAppend(),
                                    MqlUtil.qWrap((String) attr)));
                        }
                        Set attrsToAdd = set.get();
                        for (Object attr:attrsToAdd) {
                            if (!modifiable){
                                assertIsModifiable(method);
                                modifiable = true;
                            }
                            chunks.add(new ModChunk(fName, annot.addPriority(), M_ADD, annot.value(),
                                    annot.valueAppend(),
                                    MqlUtil.qWrap((String) attr)));
                        }
                    }
                }

            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ScriptBuildingException();
        }

        return chunks;
    }

    private static void assertIsModifiable(Method method){
        Annotation annot = method.getAnnotation(Unmodifiable.class);
        if (annot != null)
            throw new CIConstraintException(((Unmodifiable) annot).message());
    }

    public List<ScriptChunk> buildCreateScript(){
        List<ScriptChunk> result = new LinkedList<>();
        result.add(buildCreateChunk());
        AbstractCI defaultCI = buildDefaultCI();
        AbstractCI diffCI = defaultCI.buildDiff(this);
        result.addAll(diffCI.buildUpdateScript());
        return result;
    }

    protected ScriptChunk buildCreateChunk(){
        return new CreateChunk(getCIFullName());
    }

    public abstract AbstractCI buildDefaultCI();

    public List<ScriptChunk> buildDeleteScript(){
        List<ScriptChunk> result = new LinkedList<>();
        result.add(new DeleteChunk(getCIFullName()));
        return result;
    }


    @Override
    public int hashCode() {
        return Objects.hash(getSlothAdminType(), getCIName());
    }

    protected void assertTypeAndNameEquality(AbstractCI ci){
        if (!(ci.getClass().equals(getClass())))
            throw new CIException("CI types conflict");
        if (!ci.getCIFullName().equals(getCIFullName()))
            throw new CIException("CI type-objectName conflict");
    }

    protected void validateMode(CIDiffMode mode){
        checkModeAssertion(getDiffMode() == mode);
    }

    protected void validate(){
    }

    protected void checkModeAssertion(boolean statement, CIDiffMode mode){
        if (getDiffMode() == mode)
            checkModeAssertion(statement);
    }

    protected void checkModeAssertion(boolean statement){
        if (!statement)
            throw new CIException(MODE_VALIDATION_ERROR);
    }

    protected void checkModeAssertion(CIDiffMode mode){
        if (getDiffMode() != mode)
            throw new CIException(MODE_VALIDATION_ERROR);
    }

    protected void checkCIConstraint(boolean... statements){
        checkCIConstraint(null, statements);
    }

    protected void checkCIConstraint(String message, boolean... statements){
        for (boolean statement:statements){
            if (!statement) {
                String msg = String.format("CI constraint violation on CI '%s'", getCIFullName());
                if (message != null)
                    msg += ": " + message;
                throw new CIConstraintException(msg);
            }
        }
    }

    protected void checkCIConstraint(boolean statement, String message){
        checkCIConstraint(message, statement);
    }

    protected boolean isDiffMode(){
        return getDiffMode() == CIDiffMode.DIFF;
    }


    private enum SetterAndGetterMember {
        SETTER,
        GETTER,
        SETTER_ANNOTATION,
        GETTER_ANNOTATION
    }

    public Map<String, Object> toMap(){
        Map<String, Object> fieldsValues = initMap();

        fieldsValues.put(Y_MODE, getDiffMode());
        fieldsValues.put(Parser.ADMIN_TYPE_KEY, getSlothAdminType().getMqlKey());
        AbstractCIName ciName = getCIName();
        if (ciName instanceof StringCIName) {
            fieldsValues.put(Parser.ADMIN_TYPE_VALUE, ((StringCIName) ciName).getName());
        } else {
            fieldsValues.put(M_TYPE, ((BusCIName) ciName).getType());
            fieldsValues.put(Parser.ADMIN_TYPE_VALUE, ((BusCIName) ciName).getName());
            fieldsValues.put(M_REVISION, ((BusCIName) ciName).getRevision());
        }
        fieldsValues.put(Y_DESCRIPTION, getDescription());

        return fieldsValues;
    }

    private Map<String, Object> initMap(){
        return new TreeMap<>(YAMLKeysComparator.getInstance());
    }

    protected String[] sArray(Object... items) {
        String[] result = new String[items.length];
        for (int i=0; i<items.length; i++)
            result[i] = items[i].toString();
        return result;
    }

}
