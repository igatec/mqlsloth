package com.igatec.mqlsloth.parser.objects;

import com.igatec.mqlsloth.ci.AbstractCI;
import com.igatec.mqlsloth.ci.AdminObjectCI;
import com.igatec.mqlsloth.parser.mql.MqlParser;
import org.apache.commons.lang3.StringUtils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class AdminObjectObjectParser extends AbstractObjectParser {
    public AdminObjectObjectParser(Format format) {
        super(format);
    }

    @Override
    protected Map<String, Function> getKeyWordsMQL() {
        Map<String, Function> keyWordsToValueMakers = super.getKeyWordsMQL();

        Function<String, Collection<Map.Entry<String, String>>> propertiesMaker = this::makeValueForKeyProperty;
        keyWordsToValueMakers.put(M_PROPERTY, propertiesMaker);

        Function<String, Boolean> isHiddenMaker = Boolean::valueOf;
        keyWordsToValueMakers.put(M_HIDDEN, isHiddenMaker);

        return keyWordsToValueMakers;
    }

    protected Collection<Map.Entry<String, String>> makeValueForKeyProperty(String foundValue) {
        String[] lineParts = foundValue.split("value", 2);
        String name = null;
        String value = null;

        if (lineParts.length < 2) {
            name = foundValue.trim();
            value = StringUtils.EMPTY;
        } else if (lineParts.length == 2) {
            name = lineParts[0].trim();
            value = lineParts[1].trim();
        } else {
            return Collections.EMPTY_LIST;
        }

        List<String> nameParts = MqlParser.splitHeaderLine(name);
        List<String> valueParts = MqlParser.splitHeaderLine(value);
        name = (nameParts.size() == 1) ? nameParts.get(0) : name;
        value = (valueParts.size() == 1) ? valueParts.get(0) : value;
        Map.Entry<String, String> property = new AbstractMap.SimpleEntry(name, value);
        Collection<Map.Entry<String, String>> properties = new ArrayList<>();
        properties.add(property);
        return properties;
    }

    @Override
    protected Map<String, Function> getKeyWordsYAML() {
        Map<String, Function> keyWordsToValueMakers = super.getKeyWordsYAML();

        keyWordsToValueMakers.put(Y_PROPERTIES, Function.identity());
        keyWordsToValueMakers.put(Y_HIDDEN, Function.identity());
        keyWordsToValueMakers.put(Y_SYMBOLIC_NAME, Function.identity());

        keyWordsToValueMakers.put(Y_REMOVE_PREFIX + Y_PROPERTIES, Function.identity());

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
        if (parsedValues.containsKey(M_HIDDEN)) {
            Function<Boolean, Boolean> isHiddenMaker = Function.identity();
            ((AdminObjectCI) parsebleObject).setHidden(isHiddenMaker.apply((Boolean) parsedValues.get(M_HIDDEN)));
        }

        if (parsedValues.containsKey(M_PROPERTY)) {
            Function<
                    Collection<Map.Entry<String, String>>,
                    Collection<Map.Entry<String, String>>
                    > propertiesMaker = Function.identity();
            Collection<Map.Entry<String, String>> properties = propertiesMaker.apply(
                    (Collection<Map.Entry<String, String>>) parsedValues.getOrDefault(
                            M_PROPERTY,
                            Collections.EMPTY_MAP
                    )
            );
            properties.forEach(((AdminObjectCI) parsebleObject)::setProperty);
        }
        return;
    }

    @Override
    protected void setParsedYAMLValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedYAMLValuesToObject(parsedValues, parsebleObject);
        if (parsedValues.containsKey(Y_HIDDEN)) {
            ((AdminObjectCI) parsebleObject).setHidden((Boolean) parsedValues.get(Y_HIDDEN));
        }

        if (parsedValues.containsKey(Y_PROPERTIES)) {
            Map<String, Object> properties = (Map<String, Object>) parsedValues.getOrDefault(
                    Y_PROPERTIES,
                    Collections.EMPTY_MAP
            );
            properties.forEach((key, value) -> ((AdminObjectCI) parsebleObject).setProperty(
                    key,
                    (value == null) ? null : value.toString())
            );
        }

        if (parsedValues.containsKey(Y_SYMBOLIC_NAME)) {
            ((AdminObjectCI) parsebleObject).setSymbolicName((String) parsedValues.get(Y_SYMBOLIC_NAME));
        }

        if (parsedValues.containsKey(Y_REMOVE_PREFIX + Y_PROPERTIES)) {
            List<String> propertiesToRemove = (List<String>) parsedValues.getOrDefault(
                    Y_REMOVE_PREFIX + Y_PROPERTIES,
                    Collections.EMPTY_LIST
            );
            propertiesToRemove.forEach(((AdminObjectCI) parsebleObject)::deleteProperty);
        }

        return;
    }

    @Override
    protected void setParsedJSONValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedJSONValuesToObject(parsedValues, parsebleObject);
        return;
    }
}
