package com.igatec.mqlsloth.parser.objects;

import com.igatec.mqlsloth.ci.AbstractCI;
import com.igatec.mqlsloth.parser.ParserException;

import java.util.*;
import java.util.function.Function;

public abstract class AbstractObjectParser implements ObjectParser<AbstractCI> {
    protected Format format;

    public enum Format {
        MQL, YAML, JSON
    }

    public AbstractObjectParser(Format format) {
        this.format = format;
    }

    @Override
    public final Map<String, Function> getKeyWordsToValueMakers(){
        switch (format) {
            case MQL:
                return getKeyWordsMQL();
            case YAML:
                return getKeyWordsYAML();
            case JSON:
                return getKeyWordsJSON();
            default:
                return Collections.EMPTY_MAP;
        }
    }

    protected Map<String, Function> getKeyWordsMQL() {
        Map<String, Function> keyWordsToValueMakers = new HashMap<>();

        Function<String, String> descriptionMaker = Function.identity();
        keyWordsToValueMakers.put(M_DESCRIPTION, descriptionMaker);

        return keyWordsToValueMakers;
    }

    protected Map<String, Function> getKeyWordsYAML() {
        Map<String, Function> keyWordsToValueMakers = new HashMap<>();

        Function<String, String> modeMaker = Function.identity();
        keyWordsToValueMakers.put(Y_MODE, modeMaker);

        Function<String, String> descriptionMaker = Function.identity();
        keyWordsToValueMakers.put(Y_DESCRIPTION, descriptionMaker);

        return keyWordsToValueMakers;
    }

    protected Map<String, Function> getKeyWordsJSON() {
        Map<String, Function> keyWordsToValueMakers = new HashMap<>();
        return keyWordsToValueMakers;
    }

    @Override
    public final AbstractCI createFilledObject(Map<String, Object> fieldsValues) throws ParserException {
        AbstractCI createdObject = createObject(fieldsValues);
        setParsedValuesToObject(fieldsValues, createdObject);
        return createdObject;
    }
    protected abstract AbstractCI createObject(Map<String, Object> fieldsValues) throws ParserException;

    protected final void setParsedValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject){
        switch (format) {
            case MQL:
                setParsedMQLValuesToObject(parsedValues, parsebleObject);
                break;
            case YAML:
                setParsedYAMLValuesToObject(parsedValues, parsebleObject);
                break;
            case JSON:
                setParsedJSONValuesToObject(parsedValues, parsebleObject);
                break;
            default:
                throw new IllegalArgumentException("Format " + format + " not realized yet");
        }
    }

    protected void setParsedMQLValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        if (parsedValues.containsKey(M_DESCRIPTION)) {
            parsebleObject.setDescription((String) parsedValues.get(M_DESCRIPTION));
        }

        return;
    }

    protected void setParsedYAMLValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        if (parsedValues.containsKey(Y_DESCRIPTION)) {
            parsebleObject.setDescription((String) parsedValues.get(Y_DESCRIPTION));
        }

        return;
    }

    protected void setParsedJSONValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        parsebleObject.setDescription((String) parsedValues.get("description"));
        return;
    }

    /*
        Makes collection {"value1", "value2", "value3"}
        for mql result as follows:
            ...
            someKey value1
            someKey value2
            someKey value3
            ...
     */
    protected Collection<String> makeStringCollectionFromMultiline(String foundValue) {
        List<String> result = new LinkedList<>();
        result.add(foundValue);
        return result;
    }
}
