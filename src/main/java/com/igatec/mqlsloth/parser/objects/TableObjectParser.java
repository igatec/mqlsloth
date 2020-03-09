package com.igatec.mqlsloth.parser.objects;

import com.igatec.mqlsloth.ci.AbstractCI;
import com.igatec.mqlsloth.ci.TableCI;
import com.igatec.mqlsloth.ci.TableColumn;
import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.parser.ParserException;
import com.igatec.mqlsloth.parser.mql.MqlParser;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TableObjectParser extends AdminObjectObjectParser{
    public TableObjectParser(Format format) {
        super(format);
    }

    private TableCI tableCI;

    @Override
    protected Map<String, Function> getKeyWordsMQL() {
        Map<String, Function> keyWordMap = super.getKeyWordsMQL();
        keyWordMap.put(M_NAME, Function.identity());
        Function<String, Collection> columnMaker = MqlParser::parseListValue;
        keyWordMap.put(M_COLUMN, columnMaker);
        return keyWordMap;
    }

    @Override
    protected Map<String, Function> getKeyWordsYAML() {
        Map<String, Function> keyWordMap = super.getKeyWordsYAML();
        keyWordMap.put(Y_COLUMNS, Function.identity());
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
        if (parsedValues.containsKey(M_COLUMN)) {
            tableCI.getColumnList().addAll((ArrayDeque<TableColumn>) parsedValues.get(M_COLUMN));
        }
    }

    @Override
    protected void setParsedYAMLValuesToObject(Map<String, Object> parsedValues, AbstractCI objectToParse) {
        super.setParsedYAMLValuesToObject(parsedValues, objectToParse);
        if (parsedValues.containsKey(Y_COLUMNS)) {
            List<LinkedHashMap> columnMapList = (List<LinkedHashMap>)parsedValues.get(M_COLUMNS);
            for (LinkedHashMap columnMap : columnMapList) {
                ((TableCI) objectToParse).setColumn(columnMap);
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
        tableCI = new TableCI(name.toString(), ciDiffMode);
        return tableCI;
    }
}
