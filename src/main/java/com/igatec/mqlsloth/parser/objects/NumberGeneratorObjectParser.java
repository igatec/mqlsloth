package com.igatec.mqlsloth.parser.objects;

import com.igatec.mqlsloth.ci.AbstractCI;
import com.igatec.mqlsloth.ci.NumberGeneratorCI;
import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.parser.ParserException;

import java.util.Map;
import java.util.function.Function;

public class NumberGeneratorObjectParser extends AbstractBusObjectParser {
    public NumberGeneratorObjectParser(Format format) {
        super(format);
    }

    @Override
    protected Map<String, Function> getKeyWordsMQL() {
        Map<String, Function> keyWordsToValueMakers = super.getKeyWordsMQL();
        return keyWordsToValueMakers;
    }

    @Override
    protected Map<String, Function> getKeyWordsYAML() {
        Map<String, Function> keyWordsToValueMakers = super.getKeyWordsYAML();
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
        String revision = (String) fieldsValues.get(M_REVISION);

        if (name == null) {
            throw new ParserException("Can't create " + M_NUMBER_GENERATOR + ". Name not found");
        }

        if (revision == null) {
            throw new ParserException("Can't create " + M_NUMBER_GENERATOR + ". Revision not found");
        }

        CIDiffMode mode = CIDiffMode.valueOf((String) fieldsValues.getOrDefault("_mode", "TARGET"));
        NumberGeneratorCI createdObject = new NumberGeneratorCI(name, revision, mode);
        return createdObject;
    }

    @Override
    protected void setParsedMQLValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedMQLValuesToObject(parsedValues, parsebleObject);

        //todo uncomment it if will be needed to set field nextNumber
//        if (parsedValues.containsKey(M_ATTRIBUTE)) {
//            Map<String, Map<String, String>> attributes = (Map<String, Map<String, String>>) parsedValues.get(M_ATTRIBUTE);
//            if (attributes.containsKey(ATTR_NEXT_NUMBER)) {
//                Map<String, String> nextNumberAttr = attributes.get(ATTR_NEXT_NUMBER);
//                if (nextNumberAttr.containsKey(M_VALUE)) {
//                    String value = nextNumberAttr.get(M_VALUE);
//                    ((NumberGeneratorCI) parsebleObject).setNextNumber(value);
//                }
//            }
//        }

        return;
    }

    @Override
    protected void setParsedYAMLValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedYAMLValuesToObject(parsedValues, parsebleObject);

        //todo uncomment it  if will be needed to set field nextNumber
//        if (parsedValues.containsKey(M_ATTRIBUTE)) {
//            Map<String, String> attributes = (Map<String, String>) parsedValues.get(M_ATTRIBUTE);
//            if (attributes.containsKey(ATTR_NEXT_NUMBER)) {
//                String value = attributes.get(ATTR_NEXT_NUMBER);
//                ((NumberGeneratorCI) parsebleObject).setNextNumber(value);
//            }
//        }

        return;
    }

    @Override
    protected void setParsedJSONValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedJSONValuesToObject(parsedValues, parsebleObject);
        return;
    }

}
