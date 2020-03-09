package com.igatec.mqlsloth.parser.objects;

import com.igatec.mqlsloth.ci.*;
import com.igatec.mqlsloth.ci.constants.AccessValue;
import com.igatec.mqlsloth.parser.ParserException;
import com.igatec.mqlsloth.parser.mql.MqlParser;
import com.igatec.mqlsloth.script.MqlUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PolicyObjectParser extends AdminObjectObjectParser {

    private static final Set<String> STATE_ACCESS_RECORD_START;

    static {
        Set<String> tempSet = new HashSet<>();
        Collections.addAll(tempSet,
                M_REVOKE, M_LOGIN, M_PUBLIC, M_OWNER, M_USER
        );
        STATE_ACCESS_RECORD_START = Collections.unmodifiableSet(tempSet);
    }

    private PolicyCI createdObject;

    public PolicyObjectParser(Format format) {
        super(format);
    }

    @Override
    public Map<String, Function> getKeyWordsMQL(){
        Map<String, Function> keyWordsToValueMakers = super.getKeyWordsMQL();

        Function<String, Collection> typeMaker = MqlParser::parseListValue;
        keyWordsToValueMakers.put(M_TYPE, typeMaker);
        keyWordsToValueMakers.put(M_STORE, Function.identity());
        keyWordsToValueMakers.put(M_LOCKING, s -> s.equals(M_ENFORCED));
        keyWordsToValueMakers.put(M_MINOR__SEQUENCE, Function.identity());
        keyWordsToValueMakers.put(M_STATE, Function.identity());
        keyWordsToValueMakers.put(M_DEFAULT_FORMAT, Function.identity());
        Function<String, Collection> formatMaker = MqlParser::parseListValue;
        keyWordsToValueMakers.put(M_FORMAT, formatMaker);

        return keyWordsToValueMakers;
    }

    @Override
    protected Map<String, Function> getKeyWordsYAML() {
        Map<String, Function> keyWordsToValueMakers = super.getKeyWordsYAML();

        keyWordsToValueMakers.put(Y_TYPES, Function.identity());
        keyWordsToValueMakers.put(Y_STORE, Function.identity());
        keyWordsToValueMakers.put(Y_ENFORCE_LOCKING, Function.identity());
        keyWordsToValueMakers.put(Y_MINOR_SEQUENCE, Function.identity());
        keyWordsToValueMakers.put(Y_STATES, Function.identity());
        keyWordsToValueMakers.put(Y_ALL_STATES, Function.identity());
        keyWordsToValueMakers.put(Y_DEFAULT_FORMAT, Function.identity());
        keyWordsToValueMakers.put(Y_FORMATS, Function.identity());

        return keyWordsToValueMakers;
    }

    @Override
    protected Map<String, Function> getKeyWordsJSON() {
        Map<String, Function> keyWordsToValueMakers = super.getKeyWordsJSON();
        return keyWordsToValueMakers;
    }

    @Override
    protected AbstractCI createObject(Map<String, Object> fieldsValues) throws ParserException {
        String name = (String) fieldsValues.get(M_NAME);
        if (name == null)
            throw new ParserException("Can't create " + M_POLICY + ". Name not found");
        createdObject = new PolicyCI(name);
        return createdObject;
    }

    @Override
    protected void setParsedMQLValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedMQLValuesToObject(parsedValues, parsebleObject);
        PolicyCI obj = (PolicyCI) parsebleObject;

        if (parsedValues.containsKey(M_TYPE)) {
            // Do nothing. Types are set separately because of inheritance problem
        }
        Optional.ofNullable(parsedValues.get(M_LOCKING)).ifPresent( v -> obj.setEnforceLocking((Boolean) v));
        Optional.ofNullable(parsedValues.get(M_STORE)).ifPresent( v -> obj.setStore((String) v));
        Optional.ofNullable(parsedValues.get(M_MINOR__SEQUENCE)).ifPresent( v -> obj.setMinorSequence((String) v));
        Optional.ofNullable(parsedValues.get(M_DEFAULT_FORMAT)).ifPresent( v -> obj.setDefaultFormat((String) v));
        if (parsedValues.containsKey(M_STATE)) {
            List<String> states = Arrays.asList(((String) parsedValues.get(M_STATE)).split("#STATE_DELIMITER#"));
            states.forEach(this::makeStateFromMQL);
        }
        if (parsedValues.containsKey(M_FORMAT)){
            List formats = (List) parsedValues.get(M_FORMAT);
            formats.forEach(format -> obj.addFormat((String) format));
        }

        return;
    }

    private void makeStateFromMQL(String ciStateMql){
        LinkedList<String> stateDt = new LinkedList<>();
        Collections.addAll(stateDt, ciStateMql.split("\n"));
        String stateName = stateDt.pollFirst();
        PolicyAllstateRules state;
        if (M_ALL_STATE.equals(stateName)) {
            state = new PolicyAllstateRules(false);
            createdObject.setAllStatesRules(state);
        } else {
            state = createdObject.addState(stateName);
        }
        List<String> stateData = stateDt.stream().map( s -> s.substring(4) ).collect(Collectors.toList());
        ListIterator<String> stateLinesIter = stateData.listIterator();
        while (stateLinesIter.hasNext()){
            String stateRecord = stateLinesIter.next();
            if (stateRecord.startsWith(M_MINOR_REVISIONABLE) && state instanceof PolicyState)
                ((PolicyState)state).setMinorRevisionable(extractExplicitBooleanMQL(stateRecord));
            else if (stateRecord.startsWith(M_MAJOR_REVISIONABLE) && state instanceof PolicyState)
                ((PolicyState)state).setMajorRevisionable(extractExplicitBooleanMQL(stateRecord));
            else if (stateRecord.startsWith(M_VERSIONABLE) && state instanceof PolicyState)
                ((PolicyState)state).setVersionable(extractExplicitBooleanMQL(stateRecord));
            else if (stateRecord.startsWith(M_CHECKOUT__HISTORY) && state instanceof PolicyState)
                ((PolicyState)state).setCheckoutHistory(extractExplicitBooleanMQL(stateRecord));
            else if (stateRecord.startsWith(M_PUBLISHED) && state instanceof PolicyState)
                ((PolicyState)state).setPublished(extractExplicitBooleanMQL(stateRecord));
            else if (isAccessRecordMQL(stateRecord))
                processAccessRecordMQL(state, stateRecord);
            else if (isTriggerRecordMQL(stateRecord))
                processTriggerRecordMQL(state, stateRecord);
            else if (isSignatureRecoredMQL(stateRecord)) {
                String signatureName = stateRecord.split(" ", 2)[1];
                processSignatureRecordsMQL((PolicyState) state, signatureName, stateLinesIter);
            }
        }
    }

    private void processSignatureRecordsMQL(PolicyState state, String signatureName, ListIterator<String> recordIterator){
        PolicySignature signature = new PolicySignature(signatureName);
        while (recordIterator.hasNext()){
            String record = recordIterator.next();
            if (!record.startsWith("  ")){
                recordIterator.previous();
                break;
            }
            record = record.substring(2);
            String[] items = record.split(" ", 2);
            switch (items[0]) {
                case M_BRANCH:
                    signature.setBranch(items[1]);
                    break;
                case M_APPROVE:
                    Arrays.asList(items[1].split(",")).forEach(signature::addApprove);
                    break;
                case M_IGNORE:
                    Arrays.asList(items[1].split(",")).forEach(signature::addIgnore);
                    break;
                case M_REJECT:
                    Arrays.asList(items[1].split(",")).forEach(signature::addReject);
                    break;
                case M_FILTER:
                    signature.setFilter(items[1]);
                    break;
            }
        }
        state.addSignature(signature);
    }

    private boolean isSignatureRecoredMQL(String record){
        return record.startsWith(M_SIGNATURE + " ");
    }

    private void processAccessRecordMQL(PolicyAllstateRules state, String record){
        String first;
        Set<String> set1 = new HashSet<>();
        Collections.addAll(set1, M_PUBLIC, M_OWNER, M_USER);
        List<String> modifs = new LinkedList<>();
        while (!set1.contains(first = firstWord(record))){
            modifs.add(first);
            record = rest(record, first);
        }
        if (first.equals(M_PUBLIC) || first.equals(M_OWNER)) {
            modifs.add(first);
        }
        record = rest(record, first);
        List<String> userAndKeyWords = new LinkedList<>();
        while (!record.isEmpty() && !AccessValue.stringValues().contains(first = firstWord(record, true))){
            userAndKeyWords.add(first);
            record = rest(record, first);
        }
        int kIndex = userAndKeyWords.indexOf(M_KEY);
        if (kIndex == -1)
            kIndex = userAndKeyWords.size();
        List<String> userWords = new LinkedList<>();
        List<String> keyWords = new LinkedList<>();
        for (int i = 0; i<userAndKeyWords.size(); i++){
            if (i<kIndex) userWords.add(userAndKeyWords.get(i));
            if (i>kIndex) keyWords.add(userAndKeyWords.get(i));
        }
        String user = StringUtils.join(userWords, " ");
        String key = StringUtils.join(keyWords, " ");
        if (user.isEmpty()) user = null;
        if (key.isEmpty()) key = null;
        StateRecordKey recordKey = new StateRecordKey(modifs, user, key);

        first = firstWord(record); // Comma separated access values
        record = rest(record, first);
        List<String> accessValues = Arrays.asList(first.split(","));
        List<String> extraInfo = new LinkedList<>();
        while (!record.isEmpty() && !(first = firstWord(record)).equals(M_FILTER)){
            extraInfo.add(first);
            record = rest(record, first);
        }
        String filter = null;
        if (first.equals(M_FILTER)){
            filter = rest(record, first);
        }
        StateRecordValue recordValue = new StateRecordValue(accessValues, extraInfo, filter);

        state.addAccessRecord(recordKey, recordValue);
    }

    private void processTriggerRecordMQL(PolicyAllstateRules state, String record){
        record = rest(record, M_TRIGGER);
        String[] triggerRecs = record.split(",");
        for (String triggerRec: triggerRecs){
            String[] items = triggerRec.split("[:()]");
            TriggerKey key = TriggerKey.fromCamelCase(items[0]);
            String program = items[1];
            String input = items[2];
            TriggerValue value = new TriggerValue(program, input);
            state.addTrigger(key, value);
        }

    }

    private String firstWord(String record, boolean comma) {
        int iSpace = record.indexOf(" ");
        if (iSpace == -1) iSpace = Integer.MAX_VALUE;
        int iComma = record.indexOf(",");
        if (iComma == -1) iComma = Integer.MAX_VALUE;
        int i = comma ? Math.min(iSpace, iComma) : iSpace;
        if (i < record.length())
            return record.substring(0, i);
        else
            return record;
    }

    private String firstWord(String record){
        return firstWord(record, false);
    }

    private String rest(String record, String firstWord){
        if (record.length() == firstWord.length())
            return "";
        return record.substring(firstWord.length()+1);
    }

    private static boolean isTriggerRecordMQL(String s){
        return s.startsWith(M_TRIGGER + " ");
    }

    private static boolean isAccessRecordMQL(String s){
        String[] arr = s.split(" ");
        if (arr.length < 2)
            return false;
        return STATE_ACCESS_RECORD_START.contains(arr[0]);
    }

    private static boolean extractExplicitBooleanMQL(String s){
        String[] arr = s.split(" ");
        return Boolean.valueOf(arr[arr.length-1]);
    }

    private void modifyStateYAML(PolicyAllstateRules state, Map<String, Object> stateMap){

        if (state instanceof PolicyState) {
            PolicyState st = (PolicyState) state;
            Optional.ofNullable((Boolean) stateMap.get(Y_MINOR_REVISIONABLE)).ifPresent(st::setMinorRevisionable);
            Optional.ofNullable((Boolean) stateMap.get(Y_MAJOR_REVISIONABLE)).ifPresent(st::setMajorRevisionable);
            Optional.ofNullable((Boolean) stateMap.get(Y_VERSIONABLE)).ifPresent(st::setVersionable);
            Optional.ofNullable((Boolean) stateMap.get(Y_PUBLISHED)).ifPresent(st::setPublished);
            Optional.ofNullable((Boolean) stateMap.get(Y_CHECKOUT_HISTORY)).ifPresent(st::setCheckoutHistory);
        }

        /* ACCESS */
        Map<String, List<String>> accs = (Map<String, List<String>>) Optional.ofNullable(stateMap.get(Y_ACCESS)).orElse(new HashMap<>());
        for (String key: accs.keySet()) {
            List<String> modifs = new LinkedList<>();
            List<String> userWords = new LinkedList<>();
            boolean modifEnd = false;
            for (String word : key.split(" ")) {
                if (word.equals(M_USER)) {
                    modifEnd = true;
                    continue;
                }
                if (modifEnd) userWords.add(word);
                else modifs.add(word);
            }
            String user = null;
            if (!userWords.isEmpty())
                user = StringUtils.join(userWords, " ");

            List<String> vals = accs.get(key);
            for (String val : vals) {
                String k = null;
                if (val.startsWith(M_KEY)) {
                    val = rest(val, M_KEY);
                    List<String> keyWords = new LinkedList<>();
                    String first;
                    while (!val.isEmpty() && !AccessValue.stringValues().contains(first = firstWord(val, true))) {
                        keyWords.add(first);
                        val = rest(val, first);
                    }
                    k = StringUtils.join(keyWords, " ");
                }
                StateRecordKey recordKey = new StateRecordKey(modifs, user, k);

                String commaSeparatedAccessValues = firstWord(val); // Comma separated access values
                val = rest(val, commaSeparatedAccessValues);
                List<String> accessValues = Arrays.asList(commaSeparatedAccessValues.split(","));
                List<String> extraInfo = new LinkedList<>();
                String first = null;
                while (!val.isEmpty() && !(first = firstWord(val)).equals(M_FILTER)) {
                    extraInfo.add(first);
                    val = rest(val, first);
                }
                String filter = null;
                if (M_FILTER.equals(first)) {
                    filter = rest(val, first);
                }
                StateRecordValue recordValue = new StateRecordValue(accessValues, extraInfo, filter);

                state.addAccessRecord(recordKey, recordValue);
            }
        }

        /* TRIGGERS */
        List<String> triggers = (List<String>) Optional.ofNullable(stateMap.get(Y_TRIGGERS)).orElse(new LinkedList<>());
        for (String triggerRec: triggers){
            String action = firstWord(triggerRec);
            triggerRec = rest(triggerRec, action);
            String phase = firstWord(triggerRec);
            triggerRec = rest(triggerRec, phase);
            String[] items = triggerRec.split(" " + Y_PROGRAM + " ");
            String input = items[0];
            String program = items.length > 1 ? items[1] : null;
            state.addTrigger(
                    new TriggerKey(action, phase),
                    new TriggerValue(program, input)
            );
        }

        /* SIGNATURES */
        if (state instanceof PolicyState) {
            PolicyState st = (PolicyState) state;
            if (stateMap.containsKey(Y_SIGNATURES)){
                List sigsData = (List) stateMap.get(Y_SIGNATURES);
                for (Object sigData: sigsData){
                    Map sigMap = (Map) sigData;
                    String name = (String) sigMap.get(Y_NAME);
                    PolicySignature sig = new PolicySignature(name);
                    String branch = (String) sigMap.get(Y_BRANCH);
                    if (branch!=null && !branch.isEmpty())
                        sig.setBranch(branch);
                    Collection approve = (Collection) sigMap.get(Y_APPROVE);
                    approve.forEach( v -> sig.addApprove((String) v));
                    Collection ignore = (Collection) sigMap.get(Y_IGNORE);
                    ignore.forEach( v -> sig.addIgnore((String) v));
                    Collection reject = (Collection) sigMap.get(Y_REJECT);
                    reject.forEach( v -> sig.addReject((String) v));
                    String filter = (String) sigMap.get(Y_FILTER);
                    if (filter!=null && !filter.isEmpty())
                        sig.setFilter(filter);
                    st.addSignature(sig);
                }
            }
        }

    }

    @Override
    protected void setParsedYAMLValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedYAMLValuesToObject(parsedValues, parsebleObject);
        PolicyCI obj = (PolicyCI) parsebleObject;

        if (parsedValues.containsKey(Y_TYPES)) {
            Collection<String> types = (Collection<String>) parsedValues.getOrDefault(Y_TYPES, Collections.EMPTY_SET);
            types.forEach(obj::addType);
        }
        Optional.ofNullable(parsedValues.get(Y_STORE)).ifPresent( v -> obj.setStore((String) v));
        Optional.ofNullable(parsedValues.get(Y_ENFORCE_LOCKING)).ifPresent( v -> obj.setEnforceLocking((Boolean) v));
        Optional.ofNullable(parsedValues.get(Y_MINOR_SEQUENCE)).ifPresent( v -> obj.setMinorSequence((String) v));
        Optional.ofNullable(parsedValues.get(Y_DEFAULT_FORMAT)).ifPresent( v -> obj.setDefaultFormat((String) v));
        if (parsedValues.containsKey(Y_ALL_STATES)){
            PolicyAllstateRules state = new PolicyAllstateRules(false);
            obj.setAllStatesRules(state);
            modifyStateYAML(state, (Map<String, Object>) parsedValues.get(Y_ALL_STATES));
        }
        if (parsedValues.containsKey(Y_STATES)){
            List<Map<String, Object>> stateMaps = (List<Map<String, Object>>) parsedValues.get(Y_STATES);
            for (Map<String, Object> stateMap: stateMaps){
                String name = (String) stateMap.get(Y_NAME);
                PolicyState state = obj.addState(name);
                modifyStateYAML(state, stateMap);
            }
        }
        if (parsedValues.containsKey(Y_FORMATS)){
            List<String> formats = (List<String>) parsedValues.getOrDefault(Y_FORMATS, Collections.EMPTY_SET);
            formats.forEach(obj::addFormat);
        }

        return;
    }


    @Override
    protected void setParsedJSONValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedJSONValuesToObject(parsedValues, parsebleObject);

        //todo
        return;
    }

}
