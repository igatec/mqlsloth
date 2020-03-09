package com.igatec.mqlsloth.parser.objects;

import com.igatec.mqlsloth.ci.AbstractCI;
import com.igatec.mqlsloth.ci.ChannelCI;
import com.igatec.mqlsloth.ci.MenuCI;
import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.parser.ParserException;
import com.igatec.mqlsloth.parser.mql.MqlParser;

import java.util.*;
import java.util.function.Function;

public class ChannelObjectParser extends UIComponentObjectParser {

    public ChannelObjectParser(Format format) {
        super(format);
    }

    @Override
    public Map<String, Function> getKeyWordsMQL(){
        Map<String, Function> keyWordsToValueMakers = super.getKeyWordsMQL();
        keyWordsToValueMakers.put(M_HEIGHT, Function.identity());
        Function<String, Collection> commandsMaker = MqlParser::parseListValue;
        keyWordsToValueMakers.put(M_COMMAND, commandsMaker);
        return keyWordsToValueMakers;
    }

    @Override
    public Map<String, Function> getKeyWordsYAML(){
        Map<String, Function> keyWordsToValueMakers = super.getKeyWordsYAML();
        keyWordsToValueMakers.put(Y_HEIGHT, Function.identity());
        keyWordsToValueMakers.put(Y_COMMANDS, Function.identity());
        return keyWordsToValueMakers;
    }

    @Override
    protected void setParsedMQLValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedMQLValuesToObject(parsedValues, parsebleObject);
        ChannelCI ci = (ChannelCI) parsebleObject;
        Optional.ofNullable(parsedValues.get(M_HEIGHT)).ifPresent( value -> ci.setHeight((String) value));
        if (parsedValues.containsKey(M_COMMAND)){
            List commands = (List) parsedValues.get(M_COMMAND);
            for (Object item: commands){
                ci.addCommand((String) item);
            }
        }
    }

    @Override
    protected void setParsedYAMLValuesToObject(Map<String, Object> parsedValues, AbstractCI abstractCI) {
        super.setParsedYAMLValuesToObject(parsedValues, abstractCI);
        ChannelCI ci = (ChannelCI) abstractCI;
        Optional.ofNullable(parsedValues.get(Y_HEIGHT)).ifPresent( value -> ci.setHeight((String) value));
        if (parsedValues.containsKey(Y_COMMANDS)){
            List commands = (List) parsedValues.get(Y_COMMANDS);
            for (Object item: commands){
                ci.addCommand((String) item);
            }
        }
    }

    @Override
    protected AbstractCI createObject(Map<String, Object> fieldsValues) throws ParserException {
        String name = (String) fieldsValues.get(M_NAME);
        if (name == null) {
            throw new ParserException("Can't create " + M_CHANNEL + ". Name not found");
        }
        CIDiffMode mode = CIDiffMode .valueOf((String) fieldsValues.getOrDefault(Y_MODE, "TARGET"));
        ChannelCI createdObject = new ChannelCI(name, mode);
        return createdObject;
    }
}
