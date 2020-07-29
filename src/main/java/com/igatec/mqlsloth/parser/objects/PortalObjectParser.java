package com.igatec.mqlsloth.parser.objects;

import com.igatec.mqlsloth.ci.AbstractCI;
import com.igatec.mqlsloth.ci.PortalCI;
import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.parser.ParserException;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class PortalObjectParser extends UIComponentObjectParser {

    public PortalObjectParser(Format format) {
        super(format);
    }

    @Override
    public Map<String, Function> getKeyWordsMQL() {
        Map<String, Function> keyWordsToValueMakers = super.getKeyWordsMQL();
        Function<String, Collection<Map.Entry<String, String>>> channelsMaker = this::makeValueForKeyProperty;
        keyWordsToValueMakers.put(M_CHANNEL, channelsMaker);
        return keyWordsToValueMakers;
    }

    @Override
    public Map<String, Function> getKeyWordsYAML() {
        Map<String, Function> keyWordsToValueMakers = super.getKeyWordsYAML();
        keyWordsToValueMakers.put(Y_CHANNELS, Function.identity());
        return keyWordsToValueMakers;
    }

    @Override
    protected void setParsedMQLValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedMQLValuesToObject(parsedValues, parsebleObject);
        PortalCI ci = (PortalCI) parsebleObject;
        if (parsedValues.containsKey(M_CHANNEL)) {
            List chanelsSuperList = (List) parsedValues.get(M_CHANNEL);
            for (Object chanelsObj : chanelsSuperList) {
                String chanelsStr = (String) ((Map.Entry) chanelsObj).getKey();
                String[] channels = chanelsStr.split(",");
                for (int i = 0; i < channels.length; i++) {
                    ci.addChannel(channels[i], i == 0);
                }
            }
        }
    }

    @Override
    protected void setParsedYAMLValuesToObject(Map<String, Object> parsedValues, AbstractCI abstractCI) {
        super.setParsedYAMLValuesToObject(parsedValues, abstractCI);
        PortalCI ci = (PortalCI) abstractCI;
        if (parsedValues.containsKey(Y_CHANNELS)) {
            List subLists = (List) parsedValues.get(Y_CHANNELS);
            for (Object subListObj : subLists) {
                List subList = (List) subListObj;
                for (int i = 0; i < subList.size(); i++) {
                    String item = (String) subList.get(i);
                    ci.addChannel(item, i == 0);
                }
            }
        }
    }

    @Override
    protected AbstractCI createObject(Map<String, Object> fieldsValues) throws ParserException {
        String name = (String) fieldsValues.get(M_NAME);
        if (name == null) {
            throw new ParserException("Can't create " + M_MENU + ". Name not found");
        }
        CIDiffMode mode = CIDiffMode.valueOf((String) fieldsValues.getOrDefault(Y_MODE, "TARGET"));
        PortalCI createdObject = new PortalCI(name, mode);
        return createdObject;
    }
}
