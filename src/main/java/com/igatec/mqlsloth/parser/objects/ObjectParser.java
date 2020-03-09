package com.igatec.mqlsloth.parser.objects;

import com.igatec.mqlsloth.ci.AbstractCI;
import com.igatec.mqlsloth.parser.ParserException;
import com.igatec.mqlsloth.script.MqlKeywords;
import com.igatec.mqlsloth.script.YAMLKeywords;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface ObjectParser<C> extends MqlKeywords, YAMLKeywords {
    Map<String, Function> getKeyWordsToValueMakers();
    C createFilledObject(Map<String, Object> fieldsValues) throws ParserException;
}
