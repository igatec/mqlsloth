package com.igatec.mqlsloth.parser.objects;

import com.igatec.mqlsloth.ci.AbstractBusCI;
import com.igatec.mqlsloth.ci.AbstractCI;
import com.igatec.mqlsloth.ci.util.BusCIName;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractBusObjectParser extends AbstractObjectParser {

    public AbstractBusObjectParser(Format format) {
        super(format);
    }

    @Override
    protected Map<String, Function> getKeyWordsMQL() {
        Map<String, Function> keyWordsToValueMakers = super.getKeyWordsMQL();

        Function<String, String> policyMaker = Function.identity();
        keyWordsToValueMakers.put(M_POLICY, policyMaker);

        Function<String, String> stateMaker = Function.identity();
        keyWordsToValueMakers.put(M_STATE, stateMaker);

        Function<Map, Map> attributesMaker = Function.identity();
        keyWordsToValueMakers.put(M_ATTRIBUTE, attributesMaker);

        Function<Map, Map> fromConnectionMaker = Function.identity();
        keyWordsToValueMakers.put(M_FROM, fromConnectionMaker);

        return keyWordsToValueMakers;
    }

    @Override
    protected Map<String, Function> getKeyWordsYAML() {
        Map<String, Function> keyWordsToValueMakers = super.getKeyWordsYAML();

        Function<String, String> policyMaker = Function.identity();
        keyWordsToValueMakers.put(Y_POLICY, policyMaker);

        Function<String, String> stateMaker = Function.identity();
        keyWordsToValueMakers.put(Y_STATE, stateMaker);

        Function<String, String> vaultMaker = Function.identity();
        keyWordsToValueMakers.put(Y_VAULT, vaultMaker);

        Function<Map, Map> attributesMaker = Function.identity();
        keyWordsToValueMakers.put(Y_ATTRIBUTES, attributesMaker);

        Function<Map, Map> fromConnectionMaker = Function.identity();
        keyWordsToValueMakers.put(Y_FROM, fromConnectionMaker);

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
        AbstractBusCI ci = (AbstractBusCI) parsebleObject;
        if (parsedValues.containsKey(M_POLICY)) {
            ci.setPolicy((String) parsedValues.get(M_POLICY));
        }

        if (parsedValues.containsKey(M_STATE)) {
            ci.setState((String) parsedValues.get(M_STATE));
        }

        if (parsedValues.containsKey(M_ATTRIBUTE)) {
            List<Map.Entry> attributes = ((Map<String, Map<String, String>>) parsedValues.getOrDefault(M_ATTRIBUTE, Collections.EMPTY_MAP))
                    .entrySet()
                    .stream()
                    .map(entry -> {
                        String key = entry.getKey();
                        Map<String, String> fields = entry.getValue();
                        String value = fields.getOrDefault(M_VALUE, StringUtils.EMPTY);
                        return new AbstractMap.SimpleEntry<>(key, value);
                    })
                    .collect(Collectors.toList());
            attributes.forEach(ci::setAttribute);
        }

        if (parsedValues.containsKey(M_FROM)){
            Map from = (Map) parsedValues.get(M_FROM);
            for (Object relName: from.keySet()){
                Map to = (Map) ((Map) from.get(relName)).get(M_TO);
                String type = (String) to.get(M_TYPE);
                String name = (String) to.get(M_NAME);
                String revision = (String) to.get(M_REVISION);
                BusCIName busName = new BusCIName(type, name, revision);
                AbstractBusCI.ConnectionPointer cp = new AbstractBusCI.ConnectionPointer((String) relName, busName);
                ci.addFromConnection(cp);
            }
        }

        return;
    }

    @Override
    protected void setParsedYAMLValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedYAMLValuesToObject(parsedValues, parsebleObject);
        AbstractBusCI ci = (AbstractBusCI) parsebleObject;
        if (parsedValues.containsKey(Y_POLICY)) {
            ci.setPolicy((String) parsedValues.get(Y_POLICY));
        }

        if (parsedValues.containsKey(Y_STATE)) {
            ci.setState((String) parsedValues.get(Y_STATE));
        }

        if (parsedValues.containsKey(Y_ATTRIBUTES)) {
            ((Map<String, String>) parsedValues.getOrDefault(Y_ATTRIBUTES, Collections.EMPTY_MAP))
                    .entrySet()
                    .stream()
                    .forEach(ci::setAttribute);
        }

        if (parsedValues.containsKey(Y_FROM)){
            List from = (List) parsedValues.get(Y_FROM);
            for (Object connectionObj: from){
                Map connectionMap = (Map) connectionObj;
                String type = (String) connectionMap.get(Y_TYPE);
                String name = (String) connectionMap.get(Y_NAME);
                String revision = (String) connectionMap.get(Y_REVISION);
                BusCIName busName = new BusCIName(type, name, revision);
                AbstractBusCI.ConnectionPointer cp = new AbstractBusCI.ConnectionPointer((String) connectionMap.get(Y_REL), busName);
                ci.addFromConnection(cp);
            }
        }

        return;
    }

    @Override
    protected void setParsedJSONValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedJSONValuesToObject(parsedValues, parsebleObject);
        return;
    }
}
