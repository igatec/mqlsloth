package com.igatec.mqlsloth.parser.objects;

import com.igatec.mqlsloth.ci.AbstractCI;
import com.igatec.mqlsloth.ci.MenuCI;
import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.parser.ParserException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class MenuObjectParser extends UIComponentObjectParser {

    public MenuObjectParser(Format format) {
        super(format);
    }

    @Override
    public Map<String, Function> getKeyWordsMQL(){
        Map<String, Function> keyWordsToValueMakers = super.getKeyWordsMQL();
        keyWordsToValueMakers.put(M_CHILDREN, Function.identity());
        return keyWordsToValueMakers;
    }

    @Override
    public Map<String, Function> getKeyWordsYAML(){
        Map<String, Function> keyWordsToValueMakers = super.getKeyWordsYAML();
        keyWordsToValueMakers.put(Y_CHILDREN, Function.identity());
        return keyWordsToValueMakers;
    }

    @Override
    protected void setParsedMQLValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedMQLValuesToObject(parsedValues, parsebleObject);
        MenuCI ci = (MenuCI) parsebleObject;
        if (parsedValues.containsKey(M_CHILDREN)){
            String[] lines = ((String)parsedValues.get(M_CHILDREN)).split("\n");
            Arrays.asList(lines).forEach( line -> {
                if (!line.isEmpty()){
                    line = line.substring(4);
                    String[] parts = line.split(" ", 2);
                    ci.addChild(parts[0], parts[1]);
                }
            });
        }
    }

    @Override
    protected void setParsedYAMLValuesToObject(Map<String, Object> parsedValues, AbstractCI abstractCI) {
        super.setParsedYAMLValuesToObject(parsedValues, abstractCI);
        MenuCI ci = (MenuCI) abstractCI;
        if (parsedValues.containsKey(Y_CHILDREN)){
            List childrenLines = (List) parsedValues.get(Y_CHILDREN);
            for (Object chLine: childrenLines){
                String[] parts = ((String)chLine).split(" ", 2);
                ci.addChild(parts[0], parts[1]);
            }
        }
    }

    @Override
    protected AbstractCI createObject(Map<String, Object> fieldsValues) throws ParserException {
        String name = (String) fieldsValues.get(M_NAME);
        if (name == null) {
            throw new ParserException("Can't create " + M_MENU + ". Name not found");
        }
        CIDiffMode mode = CIDiffMode .valueOf((String) fieldsValues.getOrDefault(Y_MODE, "TARGET"));
        MenuCI createdObject = new MenuCI(name, mode);
        return createdObject;
    }
}
