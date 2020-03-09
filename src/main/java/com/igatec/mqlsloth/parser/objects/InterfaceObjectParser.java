package com.igatec.mqlsloth.parser.objects;

import com.igatec.mqlsloth.ci.AbstractCI;
import com.igatec.mqlsloth.ci.InterfaceCI;
import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.parser.ParserException;
import com.igatec.mqlsloth.parser.mql.MqlParser;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

public class InterfaceObjectParser extends TypeLikeObjectParser {

    public InterfaceObjectParser(Format format) {
        super(format);
    }

    @Override
    public Map<String, Function> getKeyWordsMQL(){
        Map<String, Function> keyWordsToValueMakers = super.getKeyWordsMQL();

        Function<String, Collection> typeMaker = MqlParser::parseListValue;
        keyWordsToValueMakers.put(M_TYPE, typeMaker);

        Function<String, Collection> relationshipMaker = MqlParser::parseListValue;
        keyWordsToValueMakers.put(M_RELATIONSHIP, relationshipMaker);

        return keyWordsToValueMakers;
    }

    @Override
    protected Map<String, Function> getKeyWordsYAML() {
        Map<String, Function> keyWordsToValueMakers = super.getKeyWordsYAML();
        keyWordsToValueMakers.put(Y_TYPES, Function.identity());
        keyWordsToValueMakers.put(Y_RELATIONSHIPS, Function.identity());

        keyWordsToValueMakers.put(Y_REMOVE_PREFIX + Y_TYPES, Function.identity());
        keyWordsToValueMakers.put(Y_REMOVE_PREFIX + Y_RELATIONSHIPS, Function.identity());

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
            throw new ParserException("Can't create " + M_INTERFACE + ". Name not found");
        }

        CIDiffMode mode = CIDiffMode .valueOf((String) fieldsValues.getOrDefault("_mode", "TARGET"));
        InterfaceCI createdObject = new InterfaceCI(name, mode);
        return createdObject;
    }

    @Override
    protected void setParsedMQLValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedMQLValuesToObject(parsedValues, parsebleObject);

        if (parsedValues.containsKey(M_TYPE)) {
            Collection<String> types = (Collection<String>) parsedValues.getOrDefault(M_TYPE, Collections.EMPTY_SET);
            types.forEach(((InterfaceCI) parsebleObject)::addType);
        }

        if (parsedValues.containsKey(M_RELATIONSHIP)) {
            Collection<String> relationships = (Collection<String>) parsedValues.getOrDefault(M_RELATIONSHIP, Collections.EMPTY_SET);
            relationships.forEach(((InterfaceCI) parsebleObject)::addRelationship);
        }

        return;
    }

    @Override
    protected void setParsedYAMLValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedYAMLValuesToObject(parsedValues, parsebleObject);

        if (parsedValues.containsKey(Y_TYPES)) {
            Collection<String> types = (Collection<String>) parsedValues.getOrDefault(Y_TYPES, Collections.EMPTY_SET);
            types.forEach(((InterfaceCI) parsebleObject)::addType);
        }

        if (parsedValues.containsKey(Y_REMOVE_PREFIX + Y_TYPES)) {
            Collection<String> typesToRemove = (Collection<String>) parsedValues.getOrDefault(Y_REMOVE_PREFIX + Y_TYPES, Collections.EMPTY_SET);
            typesToRemove.forEach(((InterfaceCI) parsebleObject)::reverseType);
        }

        if (parsedValues.containsKey(Y_RELATIONSHIPS)) {
            Collection<String> relationships = (Collection<String>) parsedValues.getOrDefault(Y_RELATIONSHIPS, Collections.EMPTY_SET);
            relationships.forEach(((InterfaceCI) parsebleObject)::addRelationship);
        }


        if (parsedValues.containsKey(Y_REMOVE_PREFIX + Y_RELATIONSHIPS)) {
            Collection<String> relationshipsToRemove = (Collection<String>) parsedValues.getOrDefault(Y_REMOVE_PREFIX + Y_RELATIONSHIPS, Collections.EMPTY_SET);
            relationshipsToRemove.forEach(((InterfaceCI) parsebleObject)::reverseRelationship);
        }

        return;
    }

    @Override
    protected void setParsedJSONValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedJSONValuesToObject(parsedValues, parsebleObject);
        return;
    }
}
