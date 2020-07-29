package com.igatec.mqlsloth.parser;

import java.util.Map;
import java.util.function.Function;

public interface KeyWordFinder {
    Map<String, Object> getValuesForKeys(Map<String, Function> keyWordsToValueMakers) throws ParserException;
}
