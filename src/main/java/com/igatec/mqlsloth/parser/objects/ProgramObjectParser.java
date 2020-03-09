package com.igatec.mqlsloth.parser.objects;

import com.igatec.mqlsloth.ci.AbstractCI;
import com.igatec.mqlsloth.ci.ProgramCI;
import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.parser.ParserException;

import java.util.Map;
import java.util.function.Function;

import static com.igatec.mqlsloth.ci.ProgramCI.Type.*;

public class ProgramObjectParser extends AdminObjectObjectParser {
    public ProgramObjectParser(Format format) {
        super(format);
    }

    @Override
    public Map<String, Function> getKeyWordsMQL(){
        Map<String, Function> keyWordsToValueMakers = super.getKeyWordsMQL();

        keyWordsToValueMakers.put(M_EXECUTE + " " + M_IMMEDIATE, value -> M_IMMEDIATE);
        keyWordsToValueMakers.put(M_JAVA, value -> M_JAVA);
        keyWordsToValueMakers.put(M_EKL, value -> M_EKL);
        keyWordsToValueMakers.put(M_MQL, value -> M_MQL);

        return keyWordsToValueMakers;
    }

    @Override
    protected Map<String, Function> getKeyWordsYAML() {
        Map<String, Function> keyWordsToValueMakers = super.getKeyWordsYAML();

        Function<String, ProgramCI.Type> typeMaker = type -> ProgramCI.Type.valueOf(type.toUpperCase());
        keyWordsToValueMakers.put(Y_TYPE, typeMaker);

        Function<String, ProgramCI.ExecutionBehaviour> executionMaker = execution -> ProgramCI.ExecutionBehaviour.valueOf(execution.toUpperCase());
        keyWordsToValueMakers.put(Y_EXECUTE, executionMaker);

        return keyWordsToValueMakers;
    }

    @Override
    protected Map<String, Function> getKeyWordsJSON() {
        Map<String, Function> keyWordsToValueMakers = super.getKeyWordsJSON();
        return keyWordsToValueMakers;
    }

    @Override
    protected AbstractCI createObject(Map<String, Object> fieldsValues) throws ParserException {
        String name = (String) fieldsValues.get(M_NAME);
        if (name == null) {
            throw new ParserException("Can't create " + M_PROGRAM + ". Name not found");
        }
        CIDiffMode mode = CIDiffMode.valueOf((String) fieldsValues.getOrDefault(Y_MODE, "TARGET"));


        ProgramCI createdObject = null;
        switch (format) {
            case MQL:
                createdObject = createObjectMQL(name, mode, fieldsValues);
                break;
            case YAML:
                createdObject = createObjectYAML(name, mode, fieldsValues);
                break;
            case JSON:
                createdObject = createObjectMQL(name, mode, fieldsValues);
                break;
        }

        return createdObject;
    }

    protected ProgramCI createObjectMQL(String name, CIDiffMode mode, Map<String, Object> fieldsValues) {
        if (fieldsValues.containsKey(M_JAVA)) {
            return new ProgramCI(name, mode);
        }

        if (fieldsValues.containsKey(M_EKL)) {
            return new ProgramCI(name, mode);
        }

        if (fieldsValues.containsKey(M_MQL)) {
            return new ProgramCI(name, mode);
        }

        return null;
    }

    protected ProgramCI createObjectYAML(String name, CIDiffMode mode, Map<String, Object> fieldsValues) {
        if (!fieldsValues.containsKey(Y_TYPE)) {
            return null;
        }

        ProgramCI.Type type = ProgramCI.Type.valueOf(((String)fieldsValues.get(Y_TYPE)).toUpperCase());
        switch (type) {
            case MQL:
                return new ProgramCI(name, mode);
            case EKL:
                return new ProgramCI(name, mode);
            case JAVA:
                return new ProgramCI(name, mode);
        }

        return null;
    }

    protected ProgramCI createObjectJSON(String name, CIDiffMode mode, Map<String, Object> fieldsValues) {
        return null;
    }

    @Override
    protected void setParsedMQLValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedMQLValuesToObject(parsedValues, parsebleObject);

        if (parsedValues.containsKey(M_EXECUTE + " " + M_IMMEDIATE)) {
            ((ProgramCI) parsebleObject).setExecutionBehaviour(ProgramCI.ExecutionBehaviour.IMMEDIATE);
        }

        if (parsedValues.containsKey(M_JAVA)) {
            ((ProgramCI) parsebleObject).setProgramType(JAVA);
        }

        if (parsedValues.containsKey(M_EKL)) {
            ((ProgramCI) parsebleObject).setProgramType(EKL);
        }

        if (parsedValues.containsKey(M_MQL)) {
            ((ProgramCI) parsebleObject).setProgramType(MQL);
        }

        return;
    }

    @Override
    protected void setParsedYAMLValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedYAMLValuesToObject(parsedValues, parsebleObject);

        if (parsedValues.containsKey(Y_EXECUTE)) {
            ((ProgramCI) parsebleObject).setExecutionBehaviour(ProgramCI.ExecutionBehaviour.valueOf(parsedValues.get(Y_EXECUTE).toString().toUpperCase()));
        }

        if (parsedValues.containsKey(Y_TYPE)) {
            ((ProgramCI) parsebleObject).setProgramType(ProgramCI.Type.valueOf(parsedValues.get(Y_TYPE).toString().toUpperCase()));
        }

        return;
    }

    @Override
    protected void setParsedJSONValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedJSONValuesToObject(parsedValues, parsebleObject);
        return;
    }
}
