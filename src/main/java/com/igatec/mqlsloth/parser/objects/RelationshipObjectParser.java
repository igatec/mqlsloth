package com.igatec.mqlsloth.parser.objects;

import com.igatec.mqlsloth.ci.AbstractCI;
import com.igatec.mqlsloth.ci.RelationshipCI;
import com.igatec.mqlsloth.ci.RelationshipCI.RelCIEnd;
import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.parser.Parser;
import com.igatec.mqlsloth.parser.ParserException;
import com.igatec.mqlsloth.parser.mql.MqlParser;
import com.igatec.mqlsloth.parser.yaml.YAMLParser;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class RelationshipObjectParser extends TypeLikeObjectParser {
    private RelationshipCI createdObject;

    public RelationshipObjectParser(Format format) {
        super(format);
    }

    @Override
    public Map<String, Function> getKeyWordsMQL(){
        Map<String, Function> keyWordsToValueMakers = super.getKeyWordsMQL();

        Function<String, Boolean> preventDuplicatesMaker = Boolean::valueOf;
        keyWordsToValueMakers.put(M_PREVENT_DUPLICATES, preventDuplicatesMaker);

        Function<String, String> fromEndMaker = Function.identity();
        keyWordsToValueMakers.put(RelationshipCI.End.FROM.toString().toLowerCase(), fromEndMaker);

        Function<String, String> toEndMaker = Function.identity();
        keyWordsToValueMakers.put(RelationshipCI.End.TO.toString().toLowerCase(), toEndMaker);

        return keyWordsToValueMakers;
    }

    @Override
    protected Map<String, Function> getKeyWordsYAML() {
        Map<String, Function> keyWordsToValueMakers = super.getKeyWordsYAML();

        keyWordsToValueMakers.put(Y_PREVENT_DUPLICATES, Function.identity());

        Function<Map<String, Object>, Map<String, Object>> fromEndMaker = Function.identity();
        keyWordsToValueMakers.put(RelationshipCI.End.FROM.toString().toLowerCase(), fromEndMaker);

        Function<Map<String, Object>, Map<String, Object>> toEndMaker = Function.identity();
        keyWordsToValueMakers.put(RelationshipCI.End.TO.toString().toLowerCase(), toEndMaker);

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

        if (name == null) {
            throw new ParserException("Can't create " + M_RELATIONSHIP + ". Name not found");
        }

        CIDiffMode mode = CIDiffMode .valueOf((String) fieldsValues.getOrDefault(Y_MODE, CIDiffMode.TARGET.toString()));
        createdObject = new RelationshipCI(name, mode);
        return createdObject;
    }

    @Override
    protected void setParsedMQLValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedMQLValuesToObject(parsedValues, parsebleObject);

        if (parsedValues.containsKey(M_PREVENT_DUPLICATES)) {
            ((RelationshipCI) parsebleObject).setPreventDuplicates((Boolean) parsedValues.get(M_PREVENT_DUPLICATES));
        }

        if (parsedValues.containsKey(RelationshipCI.End.FROM.toString().toLowerCase())) {
            Function<String, RelCIEnd> fromEndMaker = this::makeRelCIEndFromMQL;
            fromEndMaker.apply((String) parsedValues.get(RelationshipCI.End.FROM.toString().toLowerCase()));
        }

        if (parsedValues.containsKey(RelationshipCI.End.TO.toString().toLowerCase())) {
            Function<String, RelCIEnd> toEndMaker = this::makeRelCIEndToMQL;
            toEndMaker.apply((String) parsedValues.get(RelationshipCI.End.TO.toString().toLowerCase()));
        }

        return;
    }

    private RelCIEnd makeRelCIEndFromMQL(String ciEndMql){
        return makeRelCIEndMQL(ciEndMql, RelationshipCI.End.FROM);
    }

    private RelCIEnd makeRelCIEndToMQL(String ciEndMql){
        return makeRelCIEndMQL(ciEndMql, RelationshipCI.End.TO);
    }

    private RelCIEnd makeRelCIEndMQL(String ciEndMql, RelationshipCI.End end){
        try {
            Parser<RelCIEnd> parser = MqlParser.fromStringAndObjectParser(ciEndMql, new RelObjectEndParser(format, end.toString()));
            RelCIEnd relCIEnd = parser.parse();
            return relCIEnd;
        } catch (ParserException e) {
//            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void setParsedYAMLValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedYAMLValuesToObject(parsedValues, parsebleObject);

        if (parsedValues.containsKey(Y_PREVENT_DUPLICATES)) {
            ((RelationshipCI) parsebleObject).setPreventDuplicates((Boolean) parsedValues.get(Y_PREVENT_DUPLICATES));
        }

        if (parsedValues.containsKey(RelationshipCI.End.FROM.toString().toLowerCase())) {
            Function<Map<String, Object>, RelCIEnd> fromEndMaker = this::makeRelCIEndFromYAML;
            fromEndMaker.apply((Map<String, Object>) parsedValues.get(RelationshipCI.End.FROM.toString().toLowerCase()));
        }

        if (parsedValues.containsKey(RelationshipCI.End.TO.toString().toLowerCase())) {
            Function<Map<String, Object>, RelCIEnd> toEndMaker = this::makeRelCIEndToYAML;
            toEndMaker.apply((Map<String, Object>) parsedValues.get(RelationshipCI.End.TO.toString().toLowerCase()));
        }

        return;
    }

    private RelCIEnd makeRelCIEndFromYAML(Map<String, Object> ciEndYAML){
        return makeRelCIEndYAML(ciEndYAML, RelationshipCI.End.FROM);
    }

    private RelCIEnd makeRelCIEndToYAML(Map<String, Object> ciEndYAML){
        return makeRelCIEndYAML(ciEndYAML, RelationshipCI.End.TO);
    }

    private RelCIEnd makeRelCIEndYAML(Map<String, Object> ciEndYAML, RelationshipCI.End end){
        try {
            Parser<RelCIEnd> parser = YAMLParser.fromMapAndObjectParser(ciEndYAML, new RelObjectEndParser(format, end.toString()));
            RelCIEnd relCIEnd = parser.parse();
            return relCIEnd;
        } catch (ParserException e) {
//            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void setParsedJSONValuesToObject(Map<String, Object> parsedValues, AbstractCI parsebleObject) {
        super.setParsedJSONValuesToObject(parsedValues, parsebleObject);

        //todo
        return;
    }


    public class RelObjectEndParser implements ObjectParser<RelCIEnd> {
        AbstractObjectParser.Format format;
        String name;

        public RelObjectEndParser(Format format, String name) {
            this.format = format;
            this.name = name;
        }

        @Override
        public final Map<String, Function> getKeyWordsToValueMakers(){
            switch (format) {
                case MQL:
                    return getKeyWordsMQL();
                case YAML:
                    return getKeyWordsYAML();
                case JSON:
                    return getKeyWordsJSON();
                default:
                    return Collections.EMPTY_MAP;
            }
        }

        protected Map<String, Function> getKeyWordsMQL() {
            Map<String, Function> keyWordsToValueMakers = new HashMap<>();

            Function<String, Collection> typeMaker = MqlParser::parseListValue;
            keyWordsToValueMakers.put(M_TYPE, typeMaker);

            Function<String, Collection> relationshipMaker = MqlParser::parseListValue;
            keyWordsToValueMakers.put(M_RELATIONSHIP, relationshipMaker);

            keyWordsToValueMakers.put(M_MEANING, Function.identity());

            Function<String, RelationshipCI.CloneBehaviour> revisionBehaviourMaker = this::getCloneBehaviourValueOf;
            keyWordsToValueMakers.put(M_REVISION, revisionBehaviourMaker);

            Function<String, RelationshipCI.CloneBehaviour> cloneBehaviourMaker = this::getCloneBehaviourValueOf;
            keyWordsToValueMakers.put(M_CLONE, cloneBehaviourMaker);

            Function<String, Boolean> propagateModifyMaker = Boolean::valueOf;
            keyWordsToValueMakers.put(M_PROPAGATE_MODIFY, propagateModifyMaker);

            Function<String, Boolean> propagateConnectionMaker = Boolean::valueOf;
            keyWordsToValueMakers.put(M_PROPAGATE_CONNECTION, propagateConnectionMaker);

            Function<String, RelationshipCI.Cardinality> cardinalityMaker = this::getCardinalityValueOf;
            keyWordsToValueMakers.put(M_CARDINALITY, cardinalityMaker);

            return keyWordsToValueMakers;
        }

        private RelationshipCI.CloneBehaviour getCloneBehaviourValueOf(String cloneBehaviour) {
            return RelationshipCI.CloneBehaviour.valueOf(cloneBehaviour.toUpperCase());
        }

        private RelationshipCI.Cardinality getCardinalityValueOf(String cardinality) {
            return RelationshipCI.Cardinality.valueOf(cardinality.toUpperCase());
        }

        protected Map<String, Function> getKeyWordsYAML() {
            Map<String, Function> keyWordsToValueMakers = new HashMap<>();

            keyWordsToValueMakers.put(Y_TYPES, Function.identity());
            keyWordsToValueMakers.put(Y_RELATIONSHIPS, Function.identity());
            keyWordsToValueMakers.put(Y_MEANING, Function.identity());
            keyWordsToValueMakers.put(Y_REVISION, Function.identity());
            keyWordsToValueMakers.put(Y_CLONE, Function.identity());
            keyWordsToValueMakers.put(Y_PROPAGATE_MODIFY, Function.identity());
            keyWordsToValueMakers.put(Y_PROPAGATE_CONNECTION, Function.identity());
            keyWordsToValueMakers.put(Y_CARDINALITY, Function.identity());
            keyWordsToValueMakers.put(Y_REMOVE_PREFIX + Y_TYPES, Function.identity());
            keyWordsToValueMakers.put(Y_REMOVE_PREFIX + Y_RELATIONSHIPS, Function.identity());

            return keyWordsToValueMakers;
        }

        protected Map<String, Function> getKeyWordsJSON() {
            Map<String, Function> keyWordsToValueMakers = new HashMap<>();
            return keyWordsToValueMakers;
        }

        @Override
        public final RelCIEnd createFilledObject(Map<String, Object> fieldsValues) {
            RelCIEnd createdObject = createObject(fieldsValues);
            setParsedValuesToObject(fieldsValues, createdObject);
            return createdObject;
        }
        protected RelCIEnd createObject(Map<String, Object> fieldsValues){
            RelCIEnd relCIEnd = RelationshipObjectParser.this.createdObject.getEnd(RelationshipCI.End.valueOf(name.toUpperCase()));
            return relCIEnd;
        }

        protected final void setParsedValuesToObject(Map<String, Object> parsedValues, RelCIEnd parsebleObject){
            switch (format) {
                case MQL:
                    setParsedMQLValuesToObject(parsedValues, parsebleObject);
                    break;
                case YAML:
                    setParsedYAMLValuesToObject(parsedValues, parsebleObject);
                    break;
                case JSON:
                    setParsedJSONValuesToObject(parsedValues, parsebleObject);
                    break;
                default:
                    throw new IllegalArgumentException("Format " + format + " not realized yet");
            }
        }

        protected void setParsedMQLValuesToObject(Map<String, Object> parsedValues, RelCIEnd parsebleObject) {
            if (parsedValues.containsKey(M_TYPE)) {
                // Do nothing because of inheritance problem
            }

            if (parsedValues.containsKey(M_RELATIONSHIP)) {
                // Do nothing because of inheritance problem
            }

            if (parsedValues.containsKey(M_MEANING)) {
                String meaning = (String) parsedValues.get(M_MEANING);
                parsebleObject.setMeaning(meaning);
            }

            if (parsedValues.containsKey(M_REVISION)) {
                RelationshipCI.CloneBehaviour revisionBehaviour = (RelationshipCI.CloneBehaviour) parsedValues.get(M_REVISION);
                parsebleObject.setRevisionBehaviour(revisionBehaviour);
            }

            if (parsedValues.containsKey(M_CLONE)) {
                RelationshipCI.CloneBehaviour cloneBehaviour = (RelationshipCI.CloneBehaviour) parsedValues.get(M_CLONE);
                parsebleObject.setCloneBehaviour(cloneBehaviour);
            }

            if (parsedValues.containsKey(M_PROPAGATE_MODIFY)) {
                Boolean propagateModify = (Boolean) parsedValues.get(M_PROPAGATE_MODIFY);
                parsebleObject.setPropagateModify(propagateModify);
            }

            if (parsedValues.containsKey(M_PROPAGATE_CONNECTION)) {
                Boolean propagateConnection = (Boolean) parsedValues.get(M_PROPAGATE_CONNECTION);
                parsebleObject.setPropagateConnection(propagateConnection);
            }

            if (parsedValues.containsKey(M_CARDINALITY)) {
                RelationshipCI.Cardinality cardinality = (RelationshipCI.Cardinality) parsedValues.get(M_CARDINALITY);
                parsebleObject.setCardinality(cardinality);
            }

            return;
        }

        protected void setParsedYAMLValuesToObject(Map<String, Object> parsedValues, RelCIEnd parsebleObject) {
            if (parsedValues.containsKey(Y_TYPES)) {
                Collection<String> types = (Collection<String>) parsedValues.getOrDefault(Y_TYPES, Collections.EMPTY_SET);
                if (!types.contains(Y_NONE)) {
                    types.forEach(parsebleObject::addType);
                }
            }

            if (parsedValues.containsKey(Y_REMOVE_PREFIX + Y_TYPES)) {
                Collection<String> typesToRemove = (Collection<String>) parsedValues.getOrDefault(Y_REMOVE_PREFIX + Y_TYPES, Collections.EMPTY_SET);
                typesToRemove.forEach(parsebleObject::reverseType);
            }


            if (parsedValues.containsKey(Y_RELATIONSHIPS)) {
                Collection<String> relationships = (Collection<String>) parsedValues.getOrDefault(Y_RELATIONSHIPS, Collections.EMPTY_SET);
                if (!relationships.contains(Y_NONE)) {
                    relationships.forEach(parsebleObject::addRelationship);
                }
            }

            if (parsedValues.containsKey(Y_REMOVE_PREFIX + Y_RELATIONSHIPS)) {
                Collection<String> relationshipsToRemove = (Collection<String>) parsedValues.getOrDefault(Y_REMOVE_PREFIX + Y_RELATIONSHIPS, Collections.EMPTY_SET);
                relationshipsToRemove.forEach( parsebleObject::reverseRelationship);
            }

            if (parsedValues.containsKey(Y_MEANING)) {
                String meaning = (String) parsedValues.get(Y_MEANING);
                parsebleObject.setMeaning(meaning);
            }

            if (parsedValues.containsKey(Y_REVISION)) {
                RelationshipCI.CloneBehaviour revisionBehaviour = RelationshipCI.CloneBehaviour.valueOf(((String) parsedValues.get(Y_REVISION)).toUpperCase());
                parsebleObject.setRevisionBehaviour(revisionBehaviour);
            }

            if (parsedValues.containsKey(Y_CLONE)) {
                RelationshipCI.CloneBehaviour cloneBehaviour =  RelationshipCI.CloneBehaviour.valueOf(((String) parsedValues.get(Y_CLONE)).toUpperCase());
                parsebleObject.setCloneBehaviour(cloneBehaviour);
            }

            if (parsedValues.containsKey(Y_PROPAGATE_MODIFY)) {
                Boolean propagateModify = (Boolean) parsedValues.get(Y_PROPAGATE_MODIFY);
                parsebleObject.setPropagateModify(propagateModify);
            }

            if (parsedValues.containsKey(Y_PROPAGATE_CONNECTION)) {
                Boolean propagateConnection = (Boolean) parsedValues.get(Y_PROPAGATE_CONNECTION);
                parsebleObject.setPropagateConnection(propagateConnection);
            }

            if (parsedValues.containsKey(Y_CARDINALITY)) {
                RelationshipCI.Cardinality cardinality = RelationshipCI.Cardinality.valueOf(((String) parsedValues.get(Y_CARDINALITY)).toUpperCase());
                parsebleObject.setCardinality(cardinality);
            }

            return;
        }

        protected void setParsedJSONValuesToObject(Map<String, Object> parsedValues, RelCIEnd parsebleObject) {
            //todo
            return;
        }

    }
}
