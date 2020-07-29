package com.igatec.mqlsloth.parser.yaml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.igatec.mqlsloth.ci.constants.SlothAdminType;
import com.igatec.mqlsloth.parser.Parser;
import com.igatec.mqlsloth.parser.ParserException;
import com.igatec.mqlsloth.parser.objects.AbstractObjectParser;
import com.igatec.mqlsloth.parser.objects.ObjectParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

public class YAMLParser extends Parser {
    private Map<String, Object> parsedValues;
    private ObjectMapper mapper;

    public static YAMLParser fromString(String ciData) {
        YAMLParser yamlParser = new YAMLParser(ciData);
        return yamlParser;
    }

    private YAMLParser(String ciData) {
        this.format = AbstractObjectParser.Format.YAML;
        this.ciData = ciData;
        initObjectMapper();
    }

    public static YAMLParser fromMapAndObjectParser(Map<String, Object> parsedValues, ObjectParser objectParser) {
        YAMLParser yamlParser = new YAMLParser(parsedValues, objectParser);
        return yamlParser;
    }

    private YAMLParser(Map<String, Object> parsedValues, ObjectParser objectParser) {
        this.format = AbstractObjectParser.Format.YAML;
        this.parsedValues = parsedValues;
        this.objectParser = objectParser;
        initObjectMapper();
    }

    private void initObjectMapper() {
        this.mapper = new ObjectMapper(new YAMLFactory().enable(YAMLGenerator.Feature.LITERAL_BLOCK_STYLE));
    }


    @Override
    protected void beforeParseObject() throws Exception {
        if (parsedValues == null) {
            super.beforeParseObject();
        }
    }

    @Override
    protected void makeObjectParserIfNotSet() throws Exception {
        parsedValues = mapper.readValue(ciData, Map.class);
        String adminType = (String) parsedValues.get(ADMIN_TYPE_KEY);
        String objectName = (String) parsedValues.get(ADMIN_TYPE_VALUE);
        if (adminType.equals(SlothAdminType.BUS.getKey())) {
            adminType = (String) parsedValues.get(M_TYPE);
        }
        setObjectParser(getObjectParser(SlothAdminType.getByKey(adminType)));
    }

    @Override
    public Map<String, Object> getValuesForKeys(Map<String, Function> keyWordsToValueMakers) throws ParserException {
        Map<String, Object> values = new HashMap<>();
        throwIfHasUnknownKeys(keyWordsToValueMakers.keySet());

        Collection<String> allKeys = getReservedKeysForCreationObject();
        allKeys.addAll(keyWordsToValueMakers.keySet());
        for (String keyword : allKeys) {
            if (parsedValues.containsKey(keyword)) {
                Object value = parsedValues.get(keyword);
                values.put(keyword, value);
            }
        }

        return values;
    }

    private void throwIfHasUnknownKeys(Collection<String> keywords) throws ParserException {
        List<String> reservedKeys = getReservedKeysForCreationObject();
        Optional<String> message = parsedValues.keySet().stream()
                .filter(keyword -> !(keywords.contains(keyword) || reservedKeys.contains(keyword)))
                .map(s -> "\n\t".concat(s)).reduce(String::concat);

        try {
            ParserException exception = new ParserException("Unknown keys found :" + message.get());
            throw exception;
        } catch (NoSuchElementException e) {
            return;
        }
    }

    @Override
    protected List<String> getReservedKeysForCreationObject() {
        List<String> reservedKeys = new ArrayList<>();
        reservedKeys.add(ADMIN_TYPE_KEY);
        reservedKeys.add(ADMIN_TYPE_VALUE);
        reservedKeys.add(M_TYPE);
        reservedKeys.add(M_REVISION);

        return reservedKeys;
    }
}
