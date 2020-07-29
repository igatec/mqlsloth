package com.igatec.mqlsloth.parser.objects;

import com.igatec.mqlsloth.ci.AbstractCI;
import com.igatec.mqlsloth.ci.ExpressionCI;
import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.parser.ParserException;

import java.util.Map;
import java.util.function.Function;

public class ExpressionObjectParser extends AdminObjectObjectParser {
    public ExpressionObjectParser(Format format) {
        super(format);
    }

    @Override
    protected Map<String, Function> getKeyWordsMQL() {
        Map<String, Function> keyWordsToValueMakers = super.getKeyWordsMQL();
        keyWordsToValueMakers.put(M_VALUE, Function.identity());
        return keyWordsToValueMakers;
    }

    @Override
    protected Map<String, Function> getKeyWordsYAML() {
        Map<String, Function> keyWordsToValueMakers = super.getKeyWordsYAML();
        keyWordsToValueMakers.put(Y_VALUE, Function.identity());
        return keyWordsToValueMakers;
    }

    @Override
    protected Map<String, Function> getKeyWordsJSON() {
        Map<String, Function> keyWordsToValueMakers = super.getKeyWordsJSON();
        return keyWordsToValueMakers;
    }

    @Override
    protected void setParsedMQLValuesToObject(Map<String, Object> parsedValues, AbstractCI objectToParse) {
        super.setParsedMQLValuesToObject(parsedValues, objectToParse);
        if (parsedValues.containsKey(M_VALUE)) {
            ((ExpressionCI) objectToParse).setValue(parsedValues.get(M_VALUE).toString());
        }
    }

    @Override
    protected void setParsedYAMLValuesToObject(Map<String, Object> parsedValues, AbstractCI objectToParse) {
        super.setParsedYAMLValuesToObject(parsedValues, objectToParse);
        if (parsedValues.containsKey(Y_VALUE)) {
            ((ExpressionCI) objectToParse).setValue(parsedValues.get(Y_VALUE).toString());
        }
    }

    @Override
    protected void setParsedJSONValuesToObject(Map<String, Object> parsedValues, AbstractCI objectToParse) {
        super.setParsedJSONValuesToObject(parsedValues, objectToParse);
    }

    @Override
    protected AbstractCI createObject(Map<String, Object> fieldsValues) throws ParserException {
        Object name = fieldsValues.get(M_NAME);
        if (name == null) {
            throw new ParserException("Can't create" + M_EXPRESSION + ". Name not found");
        }
        CIDiffMode ciDiffMode = CIDiffMode.valueOf(fieldsValues.getOrDefault(Y_MODE, "TARGET").toString());
        ExpressionCI expressionCI = new ExpressionCI(name.toString(), ciDiffMode);
        return expressionCI;
    }
}
