package com.igatec.mqlsloth.parser.objects;

import com.igatec.mqlsloth.ci.AbstractCI;
import com.igatec.mqlsloth.ci.PageCI;
import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.parser.ParserException;

import java.util.Map;
import java.util.function.Function;

public class PageObjectParser extends AdminObjectObjectParser {
    public PageObjectParser(Format format) {
        super(format);
    }

    @Override
    public Map<String, Function> getKeyWordsMQL() {
        Map<String, Function> keyWordsToValueMakers = super.getKeyWordsMQL();
        keyWordsToValueMakers.put(M_CONTENT, Function.identity());
        keyWordsToValueMakers.put(M_MIME, Function.identity());
        return keyWordsToValueMakers;
    }

    @Override
    protected Map<String, Function> getKeyWordsYAML() {
        Map<String, Function> keyWordsToValueMakers = super.getKeyWordsYAML();
        keyWordsToValueMakers.put(Y_CONTENT, Function.identity());
        keyWordsToValueMakers.put(Y_MIME, Function.identity());
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
            throw new ParserException("Can't create " + M_PAGE + ". Name not found");
        }
        CIDiffMode mode = CIDiffMode.valueOf((String) fieldsValues.getOrDefault(Y_MODE, "TARGET"));
        PageCI createdObject = new PageCI(name, mode);
        return createdObject;
    }

    @Override
    protected void setParsedMQLValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedMQLValuesToObject(parsedValues, parsebleObject);
        if (parsedValues.containsKey(M_CONTENT)) {
            ((PageCI) parsebleObject).setContent((String) parsedValues.get(M_CONTENT));
        }
        if (parsedValues.containsKey(M_MIME)) {
            ((PageCI) parsebleObject).setMime((String) parsedValues.get(M_MIME));
        }
        return;
    }

    @Override
    protected void setParsedYAMLValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedYAMLValuesToObject(parsedValues, parsebleObject);
        if (parsedValues.containsKey(Y_CONTENT)) {
            ((PageCI) parsebleObject).setContent((String) parsedValues.get(Y_CONTENT));
        }
        if (parsedValues.containsKey(Y_MIME)) {
            ((PageCI) parsebleObject).setMime((String) parsedValues.get(Y_MIME));
        }
        return;
    }

    @Override
    protected void setParsedJSONValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedJSONValuesToObject(parsedValues, parsebleObject);
        return;
    }
}
