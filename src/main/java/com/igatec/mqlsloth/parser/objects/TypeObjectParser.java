package com.igatec.mqlsloth.parser.objects;

import com.igatec.mqlsloth.ci.AbstractCI;
import com.igatec.mqlsloth.ci.TypeCI;
import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.parser.ParserException;

import java.util.Map;
import java.util.function.Function;

public class TypeObjectParser extends TypeLikeObjectParser {

    public TypeObjectParser(Format format) {
        super(format);
    }

    @Override
    public Map<String, Function> getKeyWordsMQL() {
        Map<String, Function> keyWordsToValueMakers = super.getKeyWordsMQL();

        Function<String, Boolean> isComposedMaker = Boolean::valueOf;
        keyWordsToValueMakers.put(M_COMPOSED, isComposedMaker);

        return keyWordsToValueMakers;
    }

    @Override
    protected Map<String, Function> getKeyWordsYAML() {
        Map<String, Function> keyWordsToValueMakers = super.getKeyWordsYAML();
        keyWordsToValueMakers.put(Y_COMPOSED, Function.identity());

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
            throw new ParserException("Can't create " + M_TYPE + ". Name not found");
        }

        CIDiffMode mode = CIDiffMode.valueOf((String) fieldsValues.getOrDefault("_mode", "TARGET"));
        TypeCI createdObject = new TypeCI(name, mode);
        return createdObject;
    }

    @Override
    protected void setParsedMQLValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedMQLValuesToObject(parsedValues, parsebleObject);

        if (parsedValues.containsKey(M_COMPOSED)) {
            ((TypeCI) parsebleObject).setComposed((Boolean) parsedValues.get(M_COMPOSED));
        }

        return;
    }

    @Override
    protected void setParsedYAMLValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedYAMLValuesToObject(parsedValues, parsebleObject);

        if (parsedValues.containsKey(Y_COMPOSED)) {
            ((TypeCI) parsebleObject).setComposed((Boolean) parsedValues.get(Y_COMPOSED));
        }

        return;
    }

    @Override
    protected void setParsedJSONValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedJSONValuesToObject(parsedValues, parsebleObject);

        return;
    }
}
