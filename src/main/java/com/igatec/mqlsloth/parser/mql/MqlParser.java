package com.igatec.mqlsloth.parser.mql;

import com.igatec.mqlsloth.ci.TableColumn;
import com.igatec.mqlsloth.ci.constants.SlothAdminType;
import com.igatec.mqlsloth.parser.Parser;
import com.igatec.mqlsloth.parser.ParserException;
import com.igatec.mqlsloth.parser.objects.AbstractBusObjectParser;
import com.igatec.mqlsloth.parser.objects.AbstractObjectParser;
import com.igatec.mqlsloth.parser.objects.ObjectParser;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class MqlParser extends Parser {
    public static final String MQL_VALUES_DELIMITER = ",";
    public static final int START_NAME_INDEX = 1;
    public static final String KEY = "key";
    public static final String VALUE = "value";
    public static final String MQL_NOT_BOOLEAN_PREFIX = "not";
    protected static final String MQL_TMP_IN_VALUE_DELIMITER = "_____";
    protected static final String MQL_KEY_VALUE_DELIMITER_SPACE = "\\s+";
    protected static final String MQL_KEY_VALUE_DELIMITER_EQ = "\\s*=\\s*";
    protected static String mqlKeyValueDelimiter = MQL_KEY_VALUE_DELIMITER_SPACE;

    protected String parsebleBlock;
    protected List<String> lines;
    private Set<String> booleanFields;
    private Set<String> listFields;
    protected SlothAdminType objectType;
    protected String objectName;

    private String beforeKeySpace = null;

    public static MqlParser fromString(String ciData) {
        MqlParser mqlParser = new MqlParser(ciData);
        mqlParser.fillBooleanFields();
        mqlParser.fillListFields();
        return mqlParser;
    }

    public static MqlParser fromStringAndObjectParser(String ciData, ObjectParser objectParser) throws ParserException {
        MqlParser mqlParser = new MqlParser(ciData);
        mqlParser.setObjectParser(objectParser);
        mqlParser.fillBooleanFields();
        mqlParser.fillListFields();

        return mqlParser;
    }

    public static MqlParser fromStringAndObjectType(String ciData, SlothAdminType adminType) throws ParserException {
        MqlParser mqlParser = new MqlParser(ciData);
        mqlParser.setObjectParser(mqlParser.getObjectParser(adminType));
        mqlParser.fillBooleanFields();
        mqlParser.fillListFields();
        return mqlParser;
    }

    private MqlParser(String ciData) {
        this.format = AbstractObjectParser.Format.MQL;
        this.ciData = ciData;
    }

    public static List<String> splitHeaderLine(String line) {
        List<String> parts = new ArrayList<>();
        char space = ' ';
        char quote = '\'';
        char endSymbol = space;
        boolean recording = false;
        String value = null;
        int length = line.length();
        for (int i = 0; i < length; i++) {
            char current = line.charAt(i);
            if (!recording) {
                if (current == quote) {
                    recording = true;
                    value = "";
                    endSymbol = quote;
                } else if (current != space) {
                    recording = true;
                    value = "" + current;
                    endSymbol = space;
                }
            } else {
                if (current == endSymbol) {
                    parts.add(value);
                    recording = false;
                    value = null;
                } else {
                    value += current;
                }
            }
        }
        if (value != null) {
            parts.add(value);
        }
        return parts;
    }

    private void fillListFields() {
        listFields = new HashSet<>();

        listFields.add("attribute");
    }

    @Override
    protected void setObjectParser(ObjectParser objectParser) {
        super.setObjectParser(objectParser);
        if (objectParser instanceof AbstractBusObjectParser) {
            this.mqlKeyValueDelimiter = MQL_KEY_VALUE_DELIMITER_EQ;
        }
    }

    @Override
    protected void beforeParseObject() throws Exception {
        super.beforeParseObject();
        makeParcebleBlock();
    }

    @Override
    protected void makeObjectParserIfNotSet() throws Exception {
        makeObjectTypeAndName();
        setObjectParser(getObjectParser(objectType));
    }

    private static String unwrapQuotes(String s) {
        int length = s.length();
        if (length < 2) {
            return s;
        }
        return (s.charAt(0) == '\'' && s.charAt(length - 1) == '\'')
                ? s.substring(1, length - 1)
                : s;
    }

    private String formatHeaderLine(String headerLine) {
        String formattedLine = headerLine;

        List<String> possiblePrefixes = new ArrayList<>();
        possiblePrefixes.add("business object");
        possiblePrefixes.add("businessobject");
        possiblePrefixes.add("BusinessObject");

        for (int i = 0; i < possiblePrefixes.size(); i++) {
            String currPrefix = possiblePrefixes.get(i);
            if (!headerLine.startsWith(currPrefix)) {
                continue;
            }

            String[] parts = headerLine.split(currPrefix + "\\s*");
            formattedLine = parts[1];
            break;
        }

        return formattedLine;
    }

    protected void fillBooleanFields() {
        booleanFields = new HashSet<>();

        booleanFields.add(M_HIDDEN);
        booleanFields.add(M_MULTILINE);
        booleanFields.add(M_MULTIVALUE);
        booleanFields.add(M_RESET_ON_CLONE);
        booleanFields.add(M_RESET_ON_REVISION);
        booleanFields.add(M_PREVENT_DUPLICATES);
    }

    private Map<String, Object> makeKeyPathAndValueFromLine(String line, String mqlKeyValueDelimiter) throws ParserException {
        String[] tokens = line.split(mqlKeyValueDelimiter);
        List<String> keyPath = makeKeyPath(tokens[0]);
        String value = makeValueFromSplittedLine(tokens);

        Map<String, Object> keyPathAndValue = new HashMap<>();
        keyPathAndValue.put(KEY, keyPath);
        keyPathAndValue.put(VALUE, value);
        return keyPathAndValue;
    }

    private Map<String, Object> makeKeyPathAndValueFromLine(String line) throws ParserException {
        return makeKeyPathAndValueFromLine(line, mqlKeyValueDelimiter);
    }

    private List<String> makeKeyPath(String key) {
        String[] firstLevelParts = key.split("\\[|\\.");
        List<String> keyPath = new ArrayList<>();

        for (int i = 0; i < firstLevelParts.length; i++) {
            String currPart = firstLevelParts[i];
            String[] secondLevelParts = currPart.split("]");
            List<String> filteredParts = Stream.of(secondLevelParts).map(String::trim).collect(Collectors.toList());

            keyPath.addAll(filteredParts);
        }
        return keyPath;
    }

    private String makeValueFromSplittedLine(String[] tokens) {
        String value = "";

        int startNameIndex = START_NAME_INDEX;
        for (int i = 0; i < startNameIndex; i++) {
            if (tokens[i].isEmpty()) {
                startNameIndex++;
            }
            break;
        }

        for (int i = startNameIndex; i < tokens.length; i++) {
            value = value.concat(tokens[i] + " ");
        }
        value = value.trim();
        return value;
    }

    protected void makeObjectTypeAndName() throws ParserException {
        makeLines();
        String headerLine = "";
        for (int i = 0; i < lines.size(); i++) {
            headerLine = lines.get(i);
            if (headerLine.isEmpty()) {
                continue;
            } else {
                break;
            }
        }
        headerLine = formatHeaderLine(headerLine);
        List<String> parts = splitHeaderLine(headerLine);
//        Map<String, Object> keyPathAndValue = makeKeyPathAndValueFromLine(headerLine);
//        objectType = SlothAdminType.getByKey(((List<String>) keyPathAndValue.get(KEY)).get(0));
//        objectName = (String) keyPathAndValue.get(VALUE);
        objectType = SlothAdminType.getByKey(parts.get(0));
        parts.remove(0);
        objectName = String.join(" ", parts.toArray(new String[0]));
    }

    private String removeStartWhitespacesInAllLinesAndRemoveEmpty(String str) {
        List<String> lines = Arrays.asList(StringUtils.splitByWholeSeparatorPreserveAllTokens(str, "\n"));
        List<String> formatterLines = removeStartWhitespacesColumn(lines);
        return linesListToString(formatterLines);
    }

    protected void makeLines() {
        lines = Arrays.asList(ciData.split("\n"));
    }

    protected void makeParcebleBlock() {
        if (lines == null) {
            makeLines();
            this.parsebleBlock = ciData;
            return;
        }

        this.parsebleBlock = ciData;
        return;
    }
    // CHECKSTYLE.OFF: MethodLength

    private boolean isMultiline(String currLine) {
        return currLine.endsWith("\r\n");
    }

    private List<String> removeStartWhitespacesColumn(List<String> lines) {
        int minNWhitespaces = -1;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isEmpty()) {
                continue;
            }

            //todo check necessity to refactor thru no codePoints
            int currLineSpaceCount = 0;
            for (int j = 0; j < line.length(); j++) {
                char charAt = line.charAt(j);
                if (charAt != ' ') {
                    break;
                }

                currLineSpaceCount++;
            }

            if (minNWhitespaces < 0) {
                minNWhitespaces = currLineSpaceCount;
                continue;
            }

            if (currLineSpaceCount < minNWhitespaces) {
//                minNWhitespaces = currLineSpaceCount;
                StringBuilder tmpLine = new StringBuilder();
                int rightShift = minNWhitespaces + (minNWhitespaces - currLineSpaceCount);
                for (int j = 0; j < rightShift; j++) {
                    tmpLine.append(' ');
                }
                line = tmpLine.append(line).toString();
                lines.set(i, line);
                continue;
            }
        }

        if (minNWhitespaces < 0) {
            return lines;
        }

        List<String> formattedLines = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String currLine = lines.get(i);
            String formattedLine;
            if (currLine.isEmpty()) {
                if (i == 0) {
                    continue;
                }
                formattedLine = currLine;
            } else {
                formattedLine = currLine.substring(minNWhitespaces);
            }

            formattedLines.add(formattedLine);
        }


        return formattedLines;
    }

    protected boolean isLineStartsWithFieldName(String line) {
        if (beforeKeySpace == null) {
            fillBeforeKeySpaces(line);
            return true;
        }
        return !line.isEmpty() && line.matches(beforeKeySpace + "\\S+.*\\r*");
    }

    private void fillBeforeKeySpaces(String line) {
        beforeKeySpace = "";
        for (int j = 0; j < line.length(); j++) {
            char charAt = line.charAt(j);
            if (charAt != ' ') {
                break;
            }
            beforeKeySpace = beforeKeySpace + ' ';
        }
    }

    // todo remove ignore and refactor
    // CHECKSTYLE.OFF: MethodLength
    @Override
    public Map<String, Object> getValuesForKeys(Map<String, Function> keyWordsToValueMakers)
            throws ParserException {
        Map<String, Object> values = new HashMap<>();
        String[] lines = new String[this.lines.size() - 1];
        this.lines.subList(1, this.lines.size()).toArray(lines);
        List<String> filteredLines = new ArrayList<>();

        String prevKeyFound = null;
        String keyFound = null;
        String foundKeyValue = null;
        for (int i = 0; i < lines.length; i++) {
            String currLine = lines[i];
            if (!isLineStartsWithFieldName(currLine)) {
                if (keyFound == null) {
                    foundKeyValue = null;
                    filteredLines.add("\n" + currLine);
                } else {
                    foundKeyValue = foundKeyValue.concat("\n" + currLine);
                }
                continue;
            }
            if (keyFound != null) {
                addKeyValueToResult(values, keyFound, foundKeyValue, keyWordsToValueMakers.get(keyFound));
                prevKeyFound = keyFound;
                keyFound = null;
                foundKeyValue = null;
            }
            Iterator<String> keysIterator = keyWordsToValueMakers.keySet().iterator();
            while (keysIterator.hasNext()) {
                String currKeyword = keysIterator.next();
                if (isBooleanField(currKeyword)) {
                    if (isLineStartsWithKeyword(currLine, currKeyword)) {
                        keyFound = currKeyword;
                        foundKeyValue = String.valueOf(Boolean.TRUE);
                        break;
                    }
                    if (isLineStartsWithKeyword(currLine, MQL_NOT_BOOLEAN_PREFIX + currKeyword)) {
                        keyFound = currKeyword;
                        foundKeyValue = String.valueOf(Boolean.FALSE);
                        break;
                    }
                }
                if (isLineStartsWithKeyword(currLine, currKeyword)) {
                    keyFound = currKeyword;
                    String formattedLine = currLine;
                    if (currKeyword.matches("^.*" + mqlKeyValueDelimiter + ".*$")) {
                        String formattedKeyword = currKeyword.replaceAll(mqlKeyValueDelimiter, MQL_TMP_IN_VALUE_DELIMITER);
                        formattedLine = currLine.replaceAll(currKeyword, formattedKeyword);
                    }
                    foundKeyValue = makeValueFromSplittedLine(formattedLine.split(mqlKeyValueDelimiter));
                    break;
                }
            }
            if (keyFound == null) {
                filteredLines.add(currLine);
                if (isMultiline(currLine) && prevKeyFound != null) {
                    List<String> prevKeyPath = makeKeyPath(prevKeyFound);
                    Object prevValue = values.get(prevKeyFound);
                } else {
                    Map<String, Object> keyPathAndValue = makeKeyPathAndValueFromLine(currLine);
                    List<String> keyPath = (List<String>) keyPathAndValue.get(KEY);
                    foundKeyValue = (String) keyPathAndValue.get(VALUE);
                    addKeyValueToResult(values, keyPath, foundKeyValue, keyWordsToValueMakers.get(keyFound));
                }
            }
        }
        if (keyFound != null) {
            addKeyValueToResult(values, keyFound, foundKeyValue, keyWordsToValueMakers.get(keyFound));
            keyFound = null;
            foundKeyValue = null;
        }
        if (!values.containsKey(ADMIN_TYPE_VALUE)) {
            values.put(ADMIN_TYPE_VALUE, objectName);
        }
        // TODO parser for TableCI columns. need to refactoring
        Deque<TableColumn> columnList = new ArrayDeque<>();
        String value = "";
        for (int i = 0; i < lines.length; i++) {
            if (!columnList.isEmpty()) {
                if (lines[i].matches("\t*label\\s+.+")) {
                    value = lines[i].trim().split("\\s+", 2)[1];
                    columnList.getLast().setLabel(value);
                }
                if (lines[i].matches("\t*businessobject\\s+.+")) {
                    value = lines[i].trim().split("\\s+", 2)[1];
                    columnList.getLast().setBusinessobject(value);
                }
                if (lines[i].matches("\t*name\\s+.+")) {
                    value = lines[i].trim().split("\\s+", 2)[1];
                    columnList.getLast().setName(value);
                }
                if (lines[i].matches("\t*href\\s+.+")) {
                    value = lines[i].trim().split("\\s+", 2)[1];
                    columnList.getLast().setHref(value);
                }
            }
            if (lines[i].matches("\\s*description\\s+.+")) {
                value = lines[i].trim().split("\\s", 2)[1];
                values.put("description", value);
            }
            if (lines[i].matches("\\s*#\\d+\\s+column")) {
                values.put("column", columnList);
                columnList.add(new TableColumn());
            }
            if (lines[i].matches("\t*setting\\s+.+value\\s+.+")) {
                if (!columnList.isEmpty()) {
                    value = lines[i].trim().split("\\s+value\\s+", 2)[1];
                    String key = lines[i].trim().split("\\s+value\\s+", 2)[0].split("\\s+", 2)[1];
                    Map<String, String> settings = new HashMap();
                    settings.put(key, value);
                    columnList.getLast().getSettings().putAll(settings);
                }
            }
        }
        return values;
    }

    private boolean isLineStartsWithKeyword(String currLine, String currKeyword) {
        return currLine.matches("^" + beforeKeySpace + currKeyword + mqlKeyValueDelimiter + ".*\\r*$")
                || currLine.matches("^" + beforeKeySpace + currKeyword + "$");
    }

    public static Collection parseListValue(String value) {
        return Arrays.stream(value.split(MQL_VALUES_DELIMITER))
                .map(MqlParser::unwrapQuotes)
                .map(String::trim)
                .collect(
                        Collectors.toCollection(ArrayList::new)
                );
    }

    protected String linesListToString(List<String> lines) {
        StringBuilder parsebleBlockWithoutHeader = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            if (i == lines.size() - 1) {
                parsebleBlockWithoutHeader.append(lines.get(i));
                continue;
            }
            parsebleBlockWithoutHeader.append(lines.get(i) + "\n");
        }


        return parsebleBlockWithoutHeader.toString();
    }

    protected boolean isBooleanField(String fieldName) {
        return booleanFields.contains(fieldName);
    }

    @Override
    protected List<String> getReservedKeysForCreationObject() {
        List<String> reservedKeys = new ArrayList<>();
        reservedKeys.add(ADMIN_TYPE_KEY);
        reservedKeys.add(ADMIN_TYPE_VALUE);

        return reservedKeys;
    }

    private void addKeyValueToResult(
            Map<String, Object> values,
            List<String> keyPath,
            String foundKeyValue,
            Function valueMaker
    ) throws ParserException {
        Map<String, Object> currLevelValues = values;
        for (int i = 0; i < keyPath.size(); i++) {
            String keyToken = keyPath.get(i);
            if (keyToken.isEmpty()) {
                continue;
            }
            if (currLevelValues.containsKey(keyToken)) {
                currLevelValues = (Map<String, Object>) currLevelValues.get(keyToken);
                continue;
            }
            if (i < keyPath.size() - 1) {
                Map<String, Object> nextLevelValues = new HashMap<>();
                currLevelValues.put(keyToken, nextLevelValues);
                currLevelValues = nextLevelValues;
                continue;
            }
            addKeyValueToResult(currLevelValues, keyToken, foundKeyValue, valueMaker);
        }
    }

    private void addKeyValueToResult(
            Map<String, Object> values,
            String keyFound,
            String foundKeyValue,
            Function valueMaker
    ) throws ParserException {
        Object resultValue = (valueMaker == null) ? foundKeyValue : valueMaker.apply(foundKeyValue);
        if (resultValue instanceof String) {
            resultValue = unwrapQuotes((String) resultValue);
        }
        if (values.containsKey(keyFound)) {
            Object prevValue = values.get(keyFound);
            if (prevValue instanceof Collection) {
                if (!(resultValue instanceof Collection)) {
                    throw new ParserException(
                            String.format(
                                    "Incorrect new value %s for key %s. Previous value is collection %s",
                                    resultValue,
                                    keyFound,
                                    prevValue
                            )
                    );
                }
                ((Collection) prevValue).addAll((Collection) resultValue);
                resultValue = prevValue;
            }
        }
        if (!keyFound.equals(M_STATE)) { // TODO workaround
            values.put(keyFound, resultValue);
        } else {
            if (!values.containsKey(keyFound)) {
                values.put(keyFound, resultValue);
            } else {
                String s = (String) values.get(keyFound);
                s += "#STATE_DELIMITER#" + resultValue;
                values.put(keyFound, s);
            }
        }
        return;
    }

}
