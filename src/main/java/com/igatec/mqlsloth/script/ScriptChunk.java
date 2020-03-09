package com.igatec.mqlsloth.script;

import com.igatec.mqlsloth.ci.util.CIFullName;
import com.igatec.mqlsloth.script.assertion.Assertion;

import java.util.LinkedList;
import java.util.List;

public abstract class ScriptChunk {

    private int priority;
    private CIFullName relatedCI;
    private List<Assertion> preAsserions;
    private List<Assertion> postAsserions;

    protected ScriptChunk(CIFullName relatedCI){
        this.relatedCI = relatedCI;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority(){
        return priority;
    }

    public CIFullName getRelatedCI() {
        return relatedCI;
    }

    public abstract String[] getCommand();

    public boolean hasAssertions(){
        return hasPostAsserions() || hasPreAsserions();
    }

    public boolean hasPreAsserions(){
        return preAsserions != null && preAsserions.size()>0;
    }

    public boolean hasPostAsserions(){
        return postAsserions != null && postAsserions.size()>0;
    }

    public List<Assertion> getPreAsserions(){
        return preAsserions==null ? new LinkedList<>() : new LinkedList<>(preAsserions);
    }

    public List<Assertion> getPostAsserions(){
        return postAsserions==null ? new LinkedList<>() : new LinkedList<>(postAsserions);
    }

    public void addPreAssertion(Assertion assertion){
        if (preAsserions == null)
            preAsserions = new LinkedList<>();
        preAsserions.add(assertion);
    }

    public void addPostAssertion(Assertion assertion){
        if (postAsserions == null)
            postAsserions = new LinkedList<>();
        postAsserions.add(assertion);
    }

}
