package com.igatec.mqlsloth.parser.objects;

import com.igatec.mqlsloth.ci.AbstractCI;
import com.igatec.mqlsloth.ci.CommandCI;
import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.parser.ParserException;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

public class CommandObjectParser extends UIComponentObjectParser {

    public CommandObjectParser(Format format) {
        super(format);
    }

    @Override
    public Map<String, Function> getKeyWordsMQL() {
        Map<String, Function> keyWordsToValueMakers = super.getKeyWordsMQL();
        keyWordsToValueMakers.put(M_USER, value -> makeStringCollectionFromMultiline((String) value));
        return keyWordsToValueMakers;
    }

    @Override
    public Map<String, Function> getKeyWordsYAML() {
        Map<String, Function> keyWordsToValueMakers = super.getKeyWordsYAML();
        keyWordsToValueMakers.put(Y_USERS, Function.identity());
        return keyWordsToValueMakers;
    }

    @Override
    protected void setParsedMQLValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedMQLValuesToObject(parsedValues, parsebleObject);
        CommandCI ci = (CommandCI) parsebleObject;
        if (parsedValues.containsKey(M_USER)) {
            ((Collection) parsedValues.get(M_USER)).forEach(value -> {
                ci.addUser((String) value);
            });
        }
    }

    @Override
    protected void setParsedYAMLValuesToObject(Map<String, Object> parsedValues, AbstractCI abstractCI) {
        super.setParsedYAMLValuesToObject(parsedValues, abstractCI);
        CommandCI ci = (CommandCI) abstractCI;
        if (parsedValues.containsKey(Y_USERS)) {
            Collection users = (Collection) parsedValues.get(Y_USERS);
            users.forEach(value -> {
                ci.addUser((String) value);
            });
        }
    }

    @Override
    protected AbstractCI createObject(Map<String, Object> fieldsValues) throws ParserException {
        String name = (String) fieldsValues.get(M_NAME);
        if (name == null) {
            throw new ParserException("Can't create " + M_COMMAND + ". Name not found");
        }
        CIDiffMode mode = CIDiffMode.valueOf((String) fieldsValues.getOrDefault(Y_MODE, "TARGET"));
        CommandCI createdObject = new CommandCI(name, mode);
        return createdObject;
    }
}
