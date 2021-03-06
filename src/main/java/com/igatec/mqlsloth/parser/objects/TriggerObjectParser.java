package com.igatec.mqlsloth.parser.objects;

import com.igatec.mqlsloth.ci.AbstractCI;
import com.igatec.mqlsloth.ci.TriggerCI;
import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.parser.ParserException;

import java.util.Map;
import java.util.function.Function;

public class TriggerObjectParser extends AbstractBusObjectParser {
    public TriggerObjectParser(Format format) {
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
            throw new ParserException("Can't create " + M_TRIGGER_INPUT + ". Name not found");
        }

        if (revision == null) {
            throw new ParserException("Can't create " + M_TRIGGER_INPUT + ". Revision not found");
        }

        CIDiffMode mode = CIDiffMode .valueOf((String) fieldsValues.getOrDefault("_mode", "TARGET"));
        TriggerCI createdObject = new TriggerCI(name, revision, mode);
        return createdObject;
    }

    @Override
    protected void setParsedMQLValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedMQLValuesToObject(parsedValues, parsebleObject);
        return;
    }

    @Override
    protected void setParsedYAMLValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedYAMLValuesToObject(parsedValues, parsebleObject);

        return;
    }

    @Override
    protected void setParsedJSONValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedJSONValuesToObject(parsedValues, parsebleObject);
        return;
    }

}
