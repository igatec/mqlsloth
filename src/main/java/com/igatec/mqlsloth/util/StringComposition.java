package com.igatec.mqlsloth.util;

import java.util.HashMap;
import java.util.Map;

public class StringComposition {

    private static final String DEFAULT_DELIMITER = "-!-!-";
    private static final String DELIMITER_KEY = "!!!DELIMITER=";
    private static final String DELIMITER_END = "END";

    private final Map<String, String> parts = new HashMap<>();

    public void addPart(String key, String content){
        parts.put(key, content);
    }

    public Map<String, String> getParts(){
        return new HashMap<>(parts);
    }

    public static boolean isParsable(String string){
        return string.startsWith(DELIMITER_KEY);
    }

    public static StringComposition parse(String string){
        if (!isParsable(string))
            throw new RuntimeException("StringComposition parsing error");
        StringComposition sc = new StringComposition();
        String delimiter = string.substring(DELIMITER_KEY.length(), string.indexOf(DELIMITER_END));
        String[] parts = string.split(delimiter);
        for (int i=2; i<parts.length; i++){
            String key = parts[i++];
            String value = i<parts.length ? parts[i] : "";
            sc.addPart(key, value);
        }
        return sc;
    }

    @Override
    public String toString(){
        String delim = DEFAULT_DELIMITER;
        while (true){
            boolean contains = false;
            for (String text:parts.values()){
                if (text.contains(delim))
                    contains = true;
            }
            if (!contains)
                break;
            delim = "<" + DEFAULT_DELIMITER + ">";
        }
        StringBuilder content = new StringBuilder();
        content.append(DELIMITER_KEY);
        content.append(delim);
        content.append(DELIMITER_END);
        for (String key:parts.keySet()){
            content.append(delim);
            content.append(key);
            content.append(delim);
            content.append(parts.get(key));
        }
        return content.toString();
    }


}
