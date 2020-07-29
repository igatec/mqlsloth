package com.igatec.mqlsloth.script;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.List;

public abstract class MqlUtil implements MqlKeywords {

    public static final String SYMBOLIC_NAME_PROGRAM = "eServiceSchemaVariableMapping.tcl";

    public static String qWrap(Object text) {
        String s = text.toString();
        return "'" + (s == null ? "" : s) + "'";
    }

    public static String getJPOClassName(String jpoName) {
        return jpoName + "_mxJPO";
    }

    public static String getJPOFileName(String jpoName) {
        return getJPOClassName(jpoName) + ".java";
    }

    public static String[] splitToLines(String queryResult) {
        if (queryResult == null || queryResult.isEmpty()) {
            return new String[0];
        }
        return queryResult.split("\n");
    }

    public static List<Pair<String, String>> parseSelectPairs(String queryResult) {
        List<Pair<String, String>> result = new LinkedList<>();
        String[] lines = splitToLines(queryResult);
        for (String line : lines) {
            if (!line.isEmpty()) {
                String[] lineArr = line.split(" = ", 2);
                if (lineArr.length == 2) {
                    Pair<String, String> pair = new ImmutablePair<>(lineArr[0].trim(), lineArr[1]);
                    result.add(pair);
                }
            }
        }
        return result;
    }
}
