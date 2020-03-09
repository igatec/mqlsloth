package com.igatec.mqlsloth.ci;

import com.fasterxml.jackson.annotation.JsonValue;
import com.igatec.mqlsloth.ci.annotation.ModBooleanProvider;
import com.igatec.mqlsloth.ci.annotation.ModStringSetProvider;
import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.ci.constants.SlothAdminType;
import com.igatec.mqlsloth.ci.util.CIFullName;
import com.igatec.mqlsloth.script.ModChunk;
import com.igatec.mqlsloth.script.ScriptChunk;
import com.igatec.mqlsloth.util.ReversibleSet;
import com.igatec.mqlsloth.util.SlothSet;

import java.util.*;

public class RelationshipCI extends TypeLikeCI {

    private Boolean preventDuplicates;
    private Map<End, RelCIEnd> ends = new HashMap<>();

    public RelationshipCI(String name) {
        this(name, CIDiffMode.TARGET);
    }
    public RelationshipCI(String name, CIDiffMode diffMode) {
        super(SlothAdminType.RELATIONSHIP, name, diffMode);
        ends.put(End.FROM, new RelCIEnd(isDiffMode()));
        ends.put(End.TO, new RelCIEnd(isDiffMode()));
        if (getDiffMode() == CIDiffMode.TARGET) {
            initTarget();
        } else {
            initDiff();
        }
    }

    private void initTarget(){
        preventDuplicates = false;
    }

    private void initDiff(){
        preventDuplicates = null;
    }

    @ModBooleanProvider(value = M_PREVENT_DUPLICATES)
    public Boolean doesPreventDuplicates() {
        return preventDuplicates;
    }
    public void setPreventDuplicates(Boolean value) {
        checkModeAssertion(value != null, CIDiffMode.TARGET);
        preventDuplicates = value;
    }


    @Override
    protected void fillDiffCI(AbstractCI newCI, AbstractCI diffCI){
        super.fillDiffCI(newCI, diffCI);
        RelationshipCI newCastedCI = (RelationshipCI) newCI;
        RelationshipCI diffCastedCI = (RelationshipCI) diffCI;
        {
            Boolean value = newCastedCI.doesPreventDuplicates();
            if (value != null && !value.equals(doesPreventDuplicates()))
                diffCastedCI.setPreventDuplicates(value);
        }

        for (End end: new End[]{End.FROM, End.TO}){
            RelCIEnd thisEnd = getEnd(end);
            RelCIEnd newEnd = newCastedCI.getEnd(end);
            RelCIEnd diffEnd = diffCastedCI.getEnd(end);
            {
                ReversibleSet<String> oldValues = thisEnd.getTypes();
                ReversibleSet<String> newValues = newEnd.getTypes();
                for (String value:SlothSet.itemsToRemove(oldValues, newValues))
                    diffEnd.reverseType(value);
                for (String value:SlothSet.itemsToAdd(oldValues, newValues))
                    diffEnd.addType(value);
            }{
                ReversibleSet<String> oldValues = thisEnd.getRelationships();
                ReversibleSet<String> newValues = newEnd.getRelationships();
                for (String value:SlothSet.itemsToRemove(oldValues, newValues))
                    diffEnd.reverseRelationship(value);
                for (String value:SlothSet.itemsToAdd(oldValues, newValues))
                    diffEnd.addRelationship(value);
            }{
                String value = newEnd.getMeaning();
                if (value != null && !value.equals(thisEnd.getMeaning()))
                    diffEnd.setMeaning(value);
            }{
                CloneBehaviour value = newEnd.getRevisionBehaviour();
                if (value != null && !value.equals(thisEnd.getRevisionBehaviour()))
                    diffEnd.setRevisionBehaviour(value);
            }{
                CloneBehaviour value = newEnd.getCloneBehaviour();
                if (value != null && !value.equals(thisEnd.getCloneBehaviour()))
                    diffEnd.setCloneBehaviour(value);
            }{
                Boolean value = newEnd.getPropagateModify();
                if (value != null && !value.equals(thisEnd.getPropagateModify()))
                    diffEnd.setPropagateModify(value);
            }{
                Boolean value = newEnd.getPropagateConnection();
                if (value != null && !value.equals(thisEnd.getPropagateConnection()))
                    diffEnd.setPropagateConnection(value);
            }{
                Cardinality value = newEnd.getCardinality();
                if (value != null && !value.equals(thisEnd.getCardinality()))
                    diffEnd.setCardinality(value);
            }
        }
    }


    @Override
    public boolean isEmpty(){
        if (!super.isEmpty()) return false;
        return preventDuplicates==null && ends.get(End.FROM).isEmpty() && ends.get(End.TO).isEmpty();
    }

    @Override
    public RelationshipCI buildDiff(AbstractCI newCI) {
        RelationshipCI ci = (RelationshipCI) newCI;
        RelationshipCI diff = new RelationshipCI(getName(), CIDiffMode.DIFF);
        fillDiffCI(ci, diff);
        return diff;
    }

    @Override
    public AbstractCI buildDefaultCI(){
        return new RelationshipCI(getName());
    }

    @Override
    public List<ScriptChunk> buildUpdateScript(){
        CIFullName fName = getCIFullName();
        List<ScriptChunk> chunks = super.buildUpdateScript();

        for (End end: new End[]{End.FROM, End.TO}) {
            RelCIEnd ciEnd = getEnd(end);
            String sEnd = end.toString();
            {
                Set<String> toRemove = ciEnd.getRelationships().getReversed();
                for (String item:toRemove) {
                    chunks.add(new ModChunk(fName, sEnd, M_REMOVE, M_RELATIONSHIP, item));
                }
                Set<String> toAdd = ciEnd.getRelationships().get();
                for (String item:toAdd) {
                    chunks.add(new ModChunk(fName, SP_ADD_REL_TO, sEnd, M_ADD, M_RELATIONSHIP, item));
                }
            }{
                Set<String> toRemove = ciEnd.getTypes().getReversed();
                for (String item:toRemove) {
                    chunks.add(new ModChunk(fName, sEnd, M_REMOVE, M_TYPE, item));
                }
                Set<String> toAdd = ciEnd.getTypes().get();
                for (String item:toAdd) {
                    chunks.add(new ModChunk(fName, SP_ADD_TYPE_TO, sEnd, M_ADD, M_TYPE, item));
                }
            }{
                String val = ciEnd.getMeaning();
                if (val != null) {
                    String[] param = new String[]{sEnd, "meaning", val};
                    chunks.add(new ModChunk(fName, param));
                }
            }{
                CloneBehaviour val = ciEnd.getRevisionBehaviour();
                if (val != null) {
                    String[] param = new String[]{sEnd, "revision", val.toString()};
                    chunks.add(new ModChunk(fName, param));
                }
            }{
                CloneBehaviour val = ciEnd.getCloneBehaviour();
                if (val != null) {
                    String[] param = new String[]{sEnd, "clone", val.toString()};
                    chunks.add(new ModChunk(fName, param));
                }
            }{
                Boolean val = ciEnd.getPropagateModify();
                if (val != null) {
                    String[] param = new String[]{sEnd, (val?"":"not") + "propagatemodify"};
                    chunks.add(new ModChunk(fName, param));
                }
            }{
                Boolean val = ciEnd.getPropagateConnection();
                if (val != null) {
                    String[] param = new String[]{sEnd, (val?"":"not") + "propagateconnection"};
                    chunks.add(new ModChunk(fName, param));
                }
            }{
                Cardinality val = ciEnd.getCardinality();
                if (val != null) {
                    String[] param = new String[]{sEnd, "cardinality", val.toString()};
                    chunks.add(new ModChunk(fName, param));
                }
            }
        }
        return chunks;
    }

    public RelCIEnd getEnd(End end){
        return ends.get(end);
    }

    public class RelCIEnd {

        private ReversibleSet<String> types;
        private ReversibleSet<String> relationships;
        private String meaning;
        private CloneBehaviour revisionBehaviour;
        private CloneBehaviour cloneBehaviour;
        private Boolean propagateModify;
        private Boolean propagateConnection;
        private Cardinality cardinality;

        public RelCIEnd(boolean diffMode){
            if (diffMode)
                initDiff();
            else
                initTarget();
        }

        private void initDiff(){
            types = new SlothSet<>(true);
            relationships = new SlothSet<>(true);
            meaning = null;
            revisionBehaviour = null;
            cloneBehaviour = null;
            propagateModify = null;
            propagateConnection = null;
        }

        private void initTarget(){
            types = new SlothSet<>(false);
            relationships = new SlothSet<>(false);
            meaning = "";
            revisionBehaviour = CloneBehaviour.NONE;
            cloneBehaviour = CloneBehaviour.NONE;
            propagateModify = false;
            propagateConnection = true;
        }

        @ModStringSetProvider(value = M_TYPE, addPriority = SP_ADD_TYPE_TO)
        public ReversibleSet<String> getTypes(){
            return new SlothSet<>(types, isDiffMode());
        }
        public boolean addType(String type){
            return types.add(type);
        }
        public boolean reverseType(String type){
            checkModeAssertion(CIDiffMode.DIFF);
            return types.reverse(type);
        }

        @ModStringSetProvider(value = M_RELATIONSHIP, addPriority = SP_ADD_REL_TO)
        public ReversibleSet<String> getRelationships(){
            return new SlothSet<>(relationships, isDiffMode());
        }
        public boolean addRelationship(String rel){
            return relationships.add(rel);
        }
        public boolean reverseRelationship(String rel){
            checkModeAssertion(CIDiffMode.DIFF);
            return relationships.reverse(rel);
        }

        public void setMeaning(String meaning) {
            this.meaning = meaning;
        }

        public String getMeaning() {
            return meaning;
        }

        public void setRevisionBehaviour(CloneBehaviour revisionBehaviour) {
            this.revisionBehaviour = revisionBehaviour;
        }

        public CloneBehaviour getRevisionBehaviour() {
            return revisionBehaviour;
        }

        public void setCloneBehaviour(CloneBehaviour cloneBehaviour) {
            this.cloneBehaviour = cloneBehaviour;
        }

        public CloneBehaviour getCloneBehaviour() {
            return cloneBehaviour;
        }

        public void setPropagateModify(Boolean propagateModify) {
            this.propagateModify = propagateModify;
        }

        public Boolean getPropagateModify() {
            return propagateModify;
        }

        public void setPropagateConnection(Boolean propagateConnection) {
            this.propagateConnection = propagateConnection;
        }

        public Boolean getPropagateConnection() {
            return propagateConnection;
        }

        public void setCardinality(Cardinality cardinality) {
            this.cardinality = cardinality;
        }

        public Cardinality getCardinality() {
            return cardinality;
        }

        public boolean isEmpty(){
            return this.types.isEmpty() && relationships.isEmpty() && meaning==null && revisionBehaviour==null &&
                    cloneBehaviour==null && propagateModify==null && propagateConnection==null && cardinality==null;
        }

        public Map<String, Object> toMap() {
            Map<String, Object> fieldsValues = new HashMap<>();

            fieldsValues.put(Y_TYPES, new TreeSet<>(getTypes()));
            fieldsValues.put(Y_RELATIONSHIPS, new TreeSet<>(getRelationships()));
            fieldsValues.put(Y_MEANING, getMeaning());
            fieldsValues.put(Y_REVISION, getRevisionBehaviour());
            fieldsValues.put(Y_CLONE, getCloneBehaviour());
            fieldsValues.put(Y_PROPAGATE_MODIFY, getPropagateModify());
            fieldsValues.put(Y_PROPAGATE_CONNECTION, getPropagateConnection());
            fieldsValues.put(Y_CARDINALITY, getCardinality());

            return fieldsValues;
        }
    }

    public enum End {
        FROM, TO;

        @Override
        public String toString(){
            return super.toString().toLowerCase();
        }

    }


    public enum Cardinality {
        ONE, MANY;

        @Override
        @JsonValue
        public String toString(){
            return super.toString().toLowerCase();
        }

    }


    public enum CloneBehaviour {
        NONE, REPLICATE, FLOAT;

        @Override
        @JsonValue
        public String toString(){
            return super.toString().toLowerCase();
        }

    }

    public Map<String, Object> toMap(){
        Map<String, Object> fieldsValues = super.toMap();

        fieldsValues.put(Y_PREVENT_DUPLICATES, doesPreventDuplicates());

        ends.forEach((key, value) -> {
            fieldsValues.put(key.toString().toLowerCase(), value.toMap());
        });

        return fieldsValues;
    }
}
