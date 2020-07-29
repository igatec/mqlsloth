package com.igatec.mqlsloth.parser.objects;

import com.igatec.mqlsloth.ci.AbstractCI;
import com.igatec.mqlsloth.ci.UIComponentCI;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public abstract class UIComponentObjectParser extends AdminObjectObjectParser {

    public UIComponentObjectParser(Format format) {
        super(format);
    }

    @Override
    public Map<String, Function> getKeyWordsMQL() {
        Map<String, Function> keyWordsToValueMakers = super.getKeyWordsMQL();
        Function<String, Collection<Map.Entry<String, String>>> settingsMaker = this::makeValueForKeyProperty;
        keyWordsToValueMakers.put(M_SETTING, settingsMaker);
        keyWordsToValueMakers.put(M_LABEL, Function.identity());
        keyWordsToValueMakers.put(M_ALT, Function.identity());
        keyWordsToValueMakers.put(M_HREF, Function.identity());
        return keyWordsToValueMakers;
    }

    @Override
    public Map<String, Function> getKeyWordsYAML() {
        Map<String, Function> keyWordsToValueMakers = super.getKeyWordsYAML();
        keyWordsToValueMakers.put(Y_SETTINGS, Function.identity());
        keyWordsToValueMakers.put(Y_LABEL, Function.identity());
        keyWordsToValueMakers.put(Y_ALT, Function.identity());
        keyWordsToValueMakers.put(Y_HREF, Function.identity());
        return keyWordsToValueMakers;
    }

    @Override
    protected void setParsedMQLValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedMQLValuesToObject(parsedValues, parsebleObject);
        UIComponentCI ci = (UIComponentCI) parsebleObject;
        if (parsedValues.containsKey(M_SETTING)) {
            Collection<Map.Entry<String, String>> settings = (Collection<Map.Entry<String, String>>) parsedValues.get(M_SETTING);
            settings.forEach(setting -> {
                ci.setSetting(setting);
            });
        }
        Optional.ofNullable(parsedValues.get(M_LABEL)).ifPresent(value -> ci.setLabel((String) value));
        Optional.ofNullable(parsedValues.get(M_ALT)).ifPresent(value -> ci.setAlt((String) value));
        Optional.ofNullable(parsedValues.get(M_HREF)).ifPresent(value -> ci.setHref((String) value));
    }

    @Override
    protected void setParsedYAMLValuesToObject(Map<String, Object> parsedValues, AbstractCI abstractCI) {
        super.setParsedYAMLValuesToObject(parsedValues, abstractCI);
        UIComponentCI ci = (UIComponentCI) abstractCI;
        Optional.ofNullable(parsedValues.get(Y_LABEL)).ifPresent(value -> ci.setLabel((String) value));
        Optional.ofNullable(parsedValues.get(Y_ALT)).ifPresent(value -> ci.setAlt((String) value));
        Optional.ofNullable(parsedValues.get(Y_HREF)).ifPresent(value -> ci.setHref((String) value));
        if (parsedValues.containsKey(Y_SETTINGS)) {
            Map settings = (Map) parsedValues.get(Y_SETTINGS);
            for (Object key : settings.keySet()) {
                ci.setSetting((String) key, (String) settings.get(key));
            }
        }
    }


}
