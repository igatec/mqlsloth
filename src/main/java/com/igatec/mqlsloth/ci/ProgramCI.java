package com.igatec.mqlsloth.ci;

import com.fasterxml.jackson.annotation.JsonValue;
import com.igatec.mqlsloth.ci.annotation.ModStandaloneStringProvider;
import com.igatec.mqlsloth.ci.annotation.ModStringProvider;
import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.ci.constants.SlothAdminType;
import com.igatec.mqlsloth.ci.util.CIFullName;
import com.igatec.mqlsloth.script.JPOCompileChunk;
import com.igatec.mqlsloth.script.ScriptChunk;

import java.util.List;
import java.util.Map;

public class ProgramCI extends AdminObjectCI {

    private Type type;
    private String code;
    private ExecutionBehaviour executionBehaviour;
//    private String sourceCodeHash;

    public ProgramCI(String name) {
        this(name, CIDiffMode.TARGET);
    }

    public ProgramCI(String name, CIDiffMode diffMode) {
        this(SlothAdminType.PROGRAM, name, diffMode);
    }

    protected ProgramCI(SlothAdminType aType, String name, CIDiffMode diffMode) {
        super(aType, name, diffMode);
        if (getDiffMode() == CIDiffMode.TARGET) {
            initTarget();
        } else {
            initDiff();
        }
    }

    private void initTarget() {
        type = Type.MQL;
        code = "";
        executionBehaviour = ExecutionBehaviour.IMMEDIATE;
//        updateSourceCodeHash();
    }

    private void initDiff() {
        type = null;
        code = null;
        executionBehaviour = null;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        checkModeAssertion(code != null, CIDiffMode.TARGET);
        this.code = code;
    }

    @ModStringProvider(M_EXECUTE)
    public ExecutionBehaviour getExecutionBehaviour() {
        return executionBehaviour;
    }

    public void setExecutionBehaviour(ExecutionBehaviour executionBehaviour) {
        checkModeAssertion(executionBehaviour != null, CIDiffMode.TARGET);
        this.executionBehaviour = executionBehaviour;
    }

    @ModStandaloneStringProvider()
    public Type getProgramType() {
        return type;
    }

    public void setProgramType(Type type) {
        checkModeAssertion(type != null, CIDiffMode.TARGET);
        this.type = type;
    }

    @Override
    protected void fillDiffCI(AbstractCI newCI, AbstractCI diffCI) {
        super.fillDiffCI(newCI, diffCI);
        ProgramCI newCastedCI = (ProgramCI) newCI;
        ProgramCI diffCastedCI = (ProgramCI) diffCI;
        Type type = newCastedCI.getProgramType();
        if (type != null && !type.equals(getProgramType())) {
            diffCastedCI.setProgramType(type);
        }
        String code = newCastedCI.getCode();
        if (code != null && !code.equals(getCode())) {
            diffCastedCI.setCode(code);
        }
        ExecutionBehaviour executionBehaviour = newCastedCI.getExecutionBehaviour();
        if (executionBehaviour != null && !executionBehaviour.equals(getExecutionBehaviour())) {
            diffCastedCI.setExecutionBehaviour(executionBehaviour);
        }
        Type programType = newCastedCI.getProgramType();
        if (programType != null && (!programType.equals(getProgramType()) || programType == Type.JAVA)) { // Workaround
            diffCastedCI.setProgramType(programType);
        }
    }

    @Override
    public boolean isEmpty() {
        if (!super.isEmpty()) {
            return false;
        }
        return type == null && code == null && executionBehaviour == null;
    }

    @Override
    public ProgramCI buildDiff(AbstractCI newCI) {
        ProgramCI ci = (ProgramCI) newCI;
        ProgramCI diff = new ProgramCI(getName(), CIDiffMode.DIFF);
        fillDiffCI(ci, diff);
        return diff;
    }

    @Override
    public AbstractCI buildDefaultCI() {
        return new ProgramCI(getName());
    }

    @Override
    public List<ScriptChunk> buildUpdateScript() {
        CIFullName fName = getCIFullName();
        List<ScriptChunk> chunks = super.buildUpdateScript();

        String code = getCode();
        if (code != null) {
            if (getProgramType() == Type.JAVA) {
                chunks.add(new JPOCompileChunk(this));
            }
        }
        return chunks;
    }

    public enum Type {
        JAVA(M_JAVA),
        MQL(M_MQL),
        EKL(M_EKL),
        EXTERNAL(M_EXTERNAL);

        private final String mqlKeyword;

        Type(String mqlKeyword) {
            this.mqlKeyword = mqlKeyword;
        }

        @Override
        @JsonValue
        public String toString() {
            return mqlKeyword;
        }

    }

    public enum ExecutionBehaviour {

        IMMEDIATE(M_IMMEDIATE),
        DEFERRED(M_DEFERRED);

        private final String mqlKeyword;

        ExecutionBehaviour(String mqlKeyword) {
            this.mqlKeyword = mqlKeyword;
        }

        @Override
        @JsonValue
        public String toString() {
            return mqlKeyword;
        }
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> fieldsValues = super.toMap();

        fieldsValues.put(Y_TYPE, getProgramType());
        fieldsValues.put(Y_EXECUTE, getExecutionBehaviour());

        return fieldsValues;
    }
}
