package com.igatec.mqlsloth.parser.objects;

import com.igatec.mqlsloth.ci.AbstractCI;
import com.igatec.mqlsloth.ci.AttributeCI;
import com.igatec.mqlsloth.ci.constants.AttributeType;
import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.parser.ParserException;
import com.igatec.mqlsloth.util.SlothString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class AttributeObjectParser extends AdminObjectObjectParser {
    public AttributeObjectParser(Format format) {
        super(format);
    }

    @Override
    public Map<String, Function> getKeyWordsMQL() {
        Map<String, Function> keyWordsToValueMakers = super.getKeyWordsMQL();

        Function<String, Boolean> isMultivalueMaker = Boolean::valueOf;
        keyWordsToValueMakers.put(M_MULTIVALUE, isMultivalueMaker);

        Function<String, Boolean> isMultilineMaker = Boolean::valueOf;
        keyWordsToValueMakers.put(M_MULTILINE, isMultilineMaker);

        Function<String, SlothString> defaultMaker = SlothString::new;
        keyWordsToValueMakers.put(M_DEFAULT, defaultMaker);

        Function<String, Collection<String>> rangeMaker = this::makeValueForKeyRange;
        keyWordsToValueMakers.put(M_RANGE, rangeMaker);

        Function<String, Integer> maxLengthMaker = Integer::valueOf;
        keyWordsToValueMakers.put(M_MAX_LENGTH, maxLengthMaker);

        Function<String, Boolean> resetOnCloneMaker = Boolean::valueOf;
        keyWordsToValueMakers.put(M_RESET_ON_CLONE, resetOnCloneMaker);

        Function<String, Boolean> resetOnRevisionMaker = Boolean::valueOf;
        keyWordsToValueMakers.put(M_RESET_ON_REVISION, resetOnRevisionMaker);

        keyWordsToValueMakers.put(M_TYPE, Function.identity());

        return keyWordsToValueMakers;
    }

    private Collection<String> makeValueForKeyRange(String foundKeyValue) {
        List<String> ranges = new ArrayList<>();
        String[] valueParts = foundKeyValue.split("=");
        if (valueParts.length != 2) {
            ranges.add("");
        } else {
            ranges.add(valueParts[1].trim());
        }
        return ranges;
    }

    @Override
    protected Map<String, Function> getKeyWordsYAML() {
        Map<String, Function> keyWordsToValueMakers = super.getKeyWordsYAML();

        keyWordsToValueMakers.put(Y_MULTIVALUE, Function.identity());
        keyWordsToValueMakers.put(Y_MULTILINE, Function.identity());

        Function<String, SlothString> defaultMaker = SlothString::new;
        keyWordsToValueMakers.put(Y_DEFAULT, defaultMaker);
        keyWordsToValueMakers.put(Y_RANGES, Function.identity());
        keyWordsToValueMakers.put(Y_MAX_LENGTH, Function.identity());
        keyWordsToValueMakers.put(Y_RESET_ON_CLONE, Function.identity());
        keyWordsToValueMakers.put(Y_RESET_ON_REVISION, Function.identity());

        keyWordsToValueMakers.put(Y_REMOVE_PREFIX + Y_DEFAULT, Function.identity());
        keyWordsToValueMakers.put(Y_REMOVE_PREFIX + Y_RANGES, Function.identity());

        keyWordsToValueMakers.put(Y_TYPE, Function.identity());

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
            throw new ParserException("Can't create " + M_ATTRIBUTE + ". Name not found");
        }

        String type = (String) fieldsValues.get(M_TYPE);
        if (type == null) {
            throw new ParserException("Can't create " + M_ATTRIBUTE + ". Type not found");
        }

        CIDiffMode mode = CIDiffMode.valueOf((String) fieldsValues.getOrDefault(Y_MODE, "TARGET"));
        AttributeCI createdObject = new AttributeCI(name, AttributeType.valueOf(type.toUpperCase()), mode);
        return createdObject;
    }

    @Override
    protected void setParsedMQLValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedMQLValuesToObject(parsedValues, parsebleObject);

        if (parsedValues.containsKey(M_MULTIVALUE)) {
            ((AttributeCI) parsebleObject).setMultivalue((Boolean) parsedValues.get(M_MULTIVALUE));
        }

        if (parsedValues.containsKey(M_MULTILINE)) {
            ((AttributeCI) parsebleObject).setMultiline((Boolean) parsedValues.get(M_MULTILINE));
        }

        if (parsedValues.containsKey(M_DEFAULT)) {
            ((AttributeCI) parsebleObject).setDefaultValue((SlothString) parsedValues.get(M_DEFAULT));
        }

        if (parsedValues.containsKey(M_RANGE)) {
            Collection<String> ranges = (Collection<String>) parsedValues.getOrDefault(M_RANGE, Collections.EMPTY_SET);
            ranges.forEach(((AttributeCI) parsebleObject)::addRange);
        }

        if (parsedValues.containsKey(M_MAX_LENGTH)) {
            ((AttributeCI) parsebleObject).setMaxLength((Integer) parsedValues.get(M_MAX_LENGTH));
        }

        if (parsedValues.containsKey(M_RESET_ON_CLONE)) {
            ((AttributeCI) parsebleObject).setResetOnClone((Boolean) parsedValues.get(M_RESET_ON_CLONE));
        }

        if (parsedValues.containsKey(M_RESET_ON_REVISION)) {
            ((AttributeCI) parsebleObject).setResetOnRevision((Boolean) parsedValues.get(M_RESET_ON_REVISION));
        }

        return;
    }

    @Override
    protected void setParsedYAMLValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedYAMLValuesToObject(parsedValues, parsebleObject);

        if (parsedValues.containsKey(Y_MULTIVALUE)) {
            ((AttributeCI) parsebleObject).setMultivalue((Boolean) parsedValues.get(Y_MULTIVALUE));
        }

        if (parsedValues.containsKey(Y_MULTILINE)) {
            ((AttributeCI) parsebleObject).setMultiline((Boolean) parsedValues.get(Y_MULTILINE));
        }

        if (parsedValues.containsKey(Y_DEFAULT)) {
            ((AttributeCI) parsebleObject).setDefaultValue(new SlothString((String) parsedValues.get(Y_DEFAULT)));
        }

        if (parsedValues.containsKey(Y_RANGES)) {
            Collection<String> ranges = (Collection<String>) parsedValues.getOrDefault(Y_RANGES, Collections.EMPTY_SET);
            ranges.forEach(((AttributeCI) parsebleObject)::addRange);
        }

        if (parsedValues.containsKey(Y_MAX_LENGTH)) {
            ((AttributeCI) parsebleObject).setMaxLength((Integer) parsedValues.get(Y_MAX_LENGTH));
        }

        if (parsedValues.containsKey(Y_RESET_ON_CLONE)) {
            ((AttributeCI) parsebleObject).setResetOnClone((Boolean) parsedValues.get(Y_RESET_ON_CLONE));
        }

        if (parsedValues.containsKey(Y_RESET_ON_REVISION)) {
            ((AttributeCI) parsebleObject).setResetOnRevision((Boolean) parsedValues.get(Y_RESET_ON_REVISION));
        }

        if (parsedValues.containsKey(Y_REMOVE_PREFIX + Y_DEFAULT)) {
            ((AttributeCI) parsebleObject).deleteDefaultValue();
        }

        if (parsedValues.containsKey(Y_REMOVE_PREFIX + Y_RANGES)) {
            List<String> rangesToRemove = (List<String>) parsedValues.getOrDefault(
                    Y_REMOVE_PREFIX + Y_RANGES,
                    Collections.EMPTY_LIST
            );
            rangesToRemove.forEach(((AttributeCI) parsebleObject)::reverseRange);
        }

        return;
    }

    @Override
    protected void setParsedJSONValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedJSONValuesToObject(parsedValues, parsebleObject);
        return;
    }
}
