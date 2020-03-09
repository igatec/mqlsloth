package com.igatec.mqlsloth.parser.objects;

import com.igatec.mqlsloth.ci.AbstractCI;
import com.igatec.mqlsloth.ci.TypeLikeCI;
import com.igatec.mqlsloth.parser.mql.MqlParser;
import com.igatec.mqlsloth.util.SlothString;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class TypeLikeObjectParser extends AdminObjectObjectParser {
    public TypeLikeObjectParser(Format format) {
        super(format);
    }

    @Override
    public Map<String, Function> getKeyWordsMQL(){
        Map<String, Function> keyWordsToValueMakers = super.getKeyWordsMQL();

        Function<String, Boolean> isAbstractMaker = Boolean::valueOf;
        keyWordsToValueMakers.put(M_ABSTRACT, isAbstractMaker);

        Function<String, SlothString> parentTypeMaker = SlothString::new;
        keyWordsToValueMakers.put(M_DERIVED, parentTypeMaker);

        Function<String, Collection> attributesMaker = MqlParser::parseListValue;
        keyWordsToValueMakers.put(M_ATTRIBUTE, attributesMaker);

        return keyWordsToValueMakers;
    }

    @Override
    protected Map<String, Function> getKeyWordsYAML() {
        Map<String, Function> keyWordsToValueMakers = super.getKeyWordsYAML();

        keyWordsToValueMakers.put(Y_ABSTRACT, Function.identity());
        keyWordsToValueMakers.put(Y_DERIVED, Function.identity());
        keyWordsToValueMakers.put(Y_ATTRIBUTES, Function.identity());

        keyWordsToValueMakers.put(Y_REMOVE_PREFIX + Y_DERIVED, Function.identity());
        keyWordsToValueMakers.put(Y_REMOVE_PREFIX + Y_ATTRIBUTES, Function.identity());

        return keyWordsToValueMakers;
    }

    @Override
    protected Map<String, Function> getKeyWordsJSON() {
        Map<String, Function> keyWordsToValueMakers = super.getKeyWordsJSON();
        return keyWordsToValueMakers;
    }

    @Override
    protected void setParsedMQLValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedMQLValuesToObject(parsedValues, parsebleObject);

        if (parsedValues.containsKey(M_ABSTRACT)) {
            ((TypeLikeCI) parsebleObject).setAbstract((Boolean) parsedValues.get(M_ABSTRACT));
        }

        if (parsedValues.containsKey(M_DERIVED)) {
            ((TypeLikeCI) parsebleObject).setParentType((SlothString) parsedValues.get(M_DERIVED));
        }

        if (parsedValues.containsKey(M_ATTRIBUTE)) {
            List<String> attributes = (List<String>) parsedValues.getOrDefault(M_ATTRIBUTE, Collections.EMPTY_LIST);
            attributes.forEach(((TypeLikeCI) parsebleObject)::addAttribute);
        }

        return;
    }

    @Override
    protected void setParsedYAMLValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedYAMLValuesToObject(parsedValues, parsebleObject);

        if (parsedValues.containsKey(Y_ABSTRACT)) {
            ((TypeLikeCI) parsebleObject).setAbstract((Boolean) parsedValues.get(Y_ABSTRACT));
        }

        if (parsedValues.containsKey(Y_DERIVED)) {
            ((TypeLikeCI) parsebleObject).setParentType(new SlothString((String) parsedValues.get(Y_DERIVED)));
        }

        if (parsedValues.containsKey(Y_ATTRIBUTES)) {
            List<String> attributes = (List<String>) parsedValues.getOrDefault(Y_ATTRIBUTES, Collections.EMPTY_LIST);
            attributes.forEach(((TypeLikeCI) parsebleObject)::addAttribute);
        }

        if (parsedValues.containsKey(Y_REMOVE_PREFIX + Y_DERIVED)) {
            ((TypeLikeCI) parsebleObject).deleteParentType();
        }


        if (parsedValues.containsKey(Y_REMOVE_PREFIX + Y_ATTRIBUTES)) {
            List<String> attributesToRemove = (List<String>) parsedValues.getOrDefault(Y_REMOVE_PREFIX + Y_ATTRIBUTES, Collections.EMPTY_LIST);
            attributesToRemove.forEach(((TypeLikeCI) parsebleObject)::reverseAttribute);
        }

        return;
    }

    @Override
    protected void setParsedJSONValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedJSONValuesToObject(parsedValues, parsebleObject);
        return;
    }
}
