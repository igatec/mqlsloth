package com.igatec.mqlsloth.parser.objects;

import com.igatec.mqlsloth.ci.AbstractCI;
import com.igatec.mqlsloth.ci.FormCI;
import com.igatec.mqlsloth.ci.FormField;
import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.parser.ParserException;
import com.igatec.mqlsloth.parser.mql.MqlParser;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FormObjectParser extends AdminObjectObjectParser {

    public static final int FORM_SELECT_PARTS_NUMBER = 3;

    public FormObjectParser(Format format) {
        super(format);
    }

    private FormCI formCI;

    @Override
    protected Map<String, Function> getKeyWordsMQL() {
        Map<String, Function> keyWordMap = super.getKeyWordsMQL();
        keyWordMap.put(M_NAME, Function.identity());
        Function<String, Collection> fieldMaker = MqlParser::parseListValue;
        keyWordMap.put(M_FIELD, fieldMaker);
        return keyWordMap;
    }

    @Override
    protected Map<String, Function> getKeyWordsYAML() {
        Map<String, Function> keyWordMap = super.getKeyWordsYAML();
        keyWordMap.put(Y_FIELDS, Function.identity());
        keyWordMap.put(Y_NAME, Function.identity());
        keyWordMap.put(Y_LABEL, Function.identity());
        keyWordMap.put(Y_BUSINESSOBJECT, Function.identity());
        keyWordMap.put(Y_RELATIONSHIP, Function.identity());
        keyWordMap.put(Y_RANGE, Function.identity());
        keyWordMap.put(Y_HREF, Function.identity());
        keyWordMap.put(Y_SETTINGS, Function.identity());
        return keyWordMap;
    }

    @Override
    protected Map<String, Function> getKeyWordsJSON() {
        return super.getKeyWordsJSON();
    }

    @Override
    protected void setParsedMQLValuesToObject(Map<String, Object> parsedValues, AbstractCI objectToParse) {
        super.setParsedMQLValuesToObject(parsedValues, objectToParse);
        if (parsedValues.containsKey(M_FIELD)) {
            List<String> fields = (List<String>) parsedValues.get(M_FIELD);
            fields.forEach(this::makeFormFieldFromMQL);
        }
    }

    private void makeFormFieldFromMQL(String printedField) {
        FormField formField = formCI.addField();
        List<String> fieldRecords = Arrays.asList(
                printedField.split("\n")
        ).stream().map(s -> s.trim()).collect(Collectors.toList());
        String[] arr;
        String select = "";
        String expressionType = null;
        for (String record : fieldRecords) {
            if (record.matches("\\s*label\\s+.+")) {
                arr = record.trim().split("\\s+", 2);
                formField.setLabel(arr[1]);
            } else if (record.matches("\\s*range\\s+.+")) {
                arr = record.trim().split("\\s+", 2);
                formField.setRange(arr[1]);
            } else if (record.matches("\\s*href\\s+.+")) {
                arr = record.split("\\s+", 2);
                formField.setHref(arr[1]);
            } else if (record.startsWith(M_NAME)) {
                String name = record.split(" ", 2)[1].trim();
                formField.setName(name);
            } else if (record.startsWith(M_SETTING)) {
                processFieldSetting(formField, record);
            } else if (record.startsWith("expressiontype")) {
                expressionType = record.trim().split("\\s+", 2)[1];
            } else if (record.matches("\\s*\\d+\\s+select\\s+.+")) {
                arr = record.split("\\s+", FORM_SELECT_PARTS_NUMBER);
                if (arr.length == FORM_SELECT_PARTS_NUMBER) {
                    select = arr[2];
                }
            }
        }
        if ("businessobject".equals(expressionType)) {
            formField.setBusinessobject(select);
        } else if ("relationship".equals(expressionType)) {
            formField.setRelationship(select);
        }
    }

    private void processFieldSetting(FormField formField, String record) {
        String[] settingKeyValue = record.split(" ", 2)[1].trim().split(" value ", 2);
        String settingName = settingKeyValue[0];
        String settingValue = settingKeyValue[1];
        formField.getSettings().put(settingName, settingValue);
    }

    @Override
    protected void setParsedYAMLValuesToObject(Map<String, Object> parsedValues, AbstractCI objectToParse) {
        super.setParsedYAMLValuesToObject(parsedValues, objectToParse);
        if (parsedValues.containsKey(Y_FIELDS)) {
            List<LinkedHashMap> fieldMapList = (List<LinkedHashMap>) parsedValues.get(M_FIELDS);
            for (LinkedHashMap fieldMap : fieldMapList) {
                ((FormCI) objectToParse).setField(fieldMap);
            }
        }
    }

    @Override
    protected void setParsedJSONValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedJSONValuesToObject(parsedValues, parsebleObject);
    }

    @Override
    protected AbstractCI createObject(Map<String, Object> fieldsValues) throws ParserException {
        Object name = fieldsValues.get(M_NAME);
        if (name == null) {
            throw new ParserException("Can't create" + M_FORM + ". Name not found");
        }
        CIDiffMode ciDiffMode = CIDiffMode.valueOf(fieldsValues.getOrDefault(Y_MODE, "TARGET").toString());
        formCI = new FormCI(name.toString(), ciDiffMode);
        return formCI;
    }
}
