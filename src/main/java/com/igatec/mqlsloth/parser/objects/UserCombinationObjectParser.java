package com.igatec.mqlsloth.parser.objects;

import com.igatec.mqlsloth.ci.AbstractCI;
import com.igatec.mqlsloth.ci.TypeLikeCI;
import com.igatec.mqlsloth.ci.UserCombinationCI;
import com.igatec.mqlsloth.parser.mql.MqlParser;
import com.igatec.mqlsloth.util.SlothString;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class UserCombinationObjectParser extends AdminObjectObjectParser {
    public UserCombinationObjectParser(Format format) {
        super(format);
    }

    @Override
    public Map<String, Function> getKeyWordsMQL(){
        Map<String, Function> keyWordsToValueMakers = super.getKeyWordsMQL();

        Function<String, String> isAbstractMaker = Function.identity();
        keyWordsToValueMakers.put(M_PARENT, isAbstractMaker);

        return keyWordsToValueMakers;
    }

    @Override
    protected Map<String, Function> getKeyWordsYAML() {
        Map<String, Function> keyWordsToValueMakers = super.getKeyWordsYAML();

        Function<String, String> isAbstractMaker = Function.identity();
        keyWordsToValueMakers.put(Y_PARENT, isAbstractMaker);

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

        if (parsedValues.containsKey(M_PARENT)) {
            ((UserCombinationCI) parsebleObject).setParent((String) parsedValues.get(M_PARENT));
        }

        return;
    }

    @Override
    protected void setParsedYAMLValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedYAMLValuesToObject(parsedValues, parsebleObject);

        if (parsedValues.containsKey(Y_PARENT)) {
            ((UserCombinationCI) parsebleObject).setParent((String) parsedValues.get(Y_PARENT));
        }

        return;
    }

    @Override
    protected void setParsedJSONValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedJSONValuesToObject(parsedValues, parsebleObject);
        return;
    }
}
