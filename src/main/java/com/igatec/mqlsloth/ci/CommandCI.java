package com.igatec.mqlsloth.ci;

import com.igatec.mqlsloth.ci.annotation.ModStringSetProvider;
import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.ci.constants.SlothAdminType;
import com.igatec.mqlsloth.util.ReversibleSet;
import com.igatec.mqlsloth.util.SlothSet;
import java.util.*;

public class CommandCI extends UIComponentCI {

    private ReversibleSet<String> users;

    public CommandCI(String name){
        this(name, CIDiffMode.TARGET);
    }
    public CommandCI(String name, CIDiffMode diffMode) {
        super(SlothAdminType.COMMAND, name, diffMode);
        if (getDiffMode() == CIDiffMode.TARGET) {
            initTarget();
        } else {
            initDiff();
        }
    }
    private void initDiff(){
        users = new SlothSet<>(true);
    }
    private void initTarget(){
        users = new SlothSet<>(false);
    }


    @ModStringSetProvider(value = M_USER, addPriority = SP_AFTER_ADMIN_CREATION_1)
    public ReversibleSet<String> getUsers(){
        return new SlothSet<>(users, isDiffMode());
    }
    public boolean addUser(String value){
        return users.add(value);
    }
    public boolean reverseUser(String value){
        checkModeAssertion(CIDiffMode.DIFF);
        return users.reverse(value);
    }

    @Override
    protected void fillDiffCI(AbstractCI newCI, AbstractCI diffCI){
        super.fillDiffCI(newCI, diffCI);
        CommandCI newCastedCI = (CommandCI) newCI;
        CommandCI diffCastedCI = (CommandCI) diffCI;
        {
            ReversibleSet<String> oldValues = getUsers();
            ReversibleSet<String> newValues = newCastedCI.getUsers();
            for (String value:SlothSet.itemsToRemove(oldValues, newValues))
                diffCastedCI.reverseUser(value);
            for (String value:SlothSet.itemsToAdd(oldValues, newValues))
                diffCastedCI.addUser(value);
        }
    }

    public boolean isEmpty(){
        if (!super.isEmpty()) return false;
        return users.isEmpty();
    }

    @Override
    public AbstractCI buildDiff(AbstractCI newCI) {
        CommandCI ci = (CommandCI) newCI;
        CommandCI diff = new CommandCI(getName(), CIDiffMode.DIFF);
        fillDiffCI(ci, diff);
        return diff;
    }

    @Override
    public AbstractCI buildDefaultCI() {
        return new CommandCI(getName());
    }

    public Map<String, Object> toMap(){
        Map<String, Object> fieldsValues = super.toMap();

        fieldsValues.put(Y_USERS, getUsers());

        return fieldsValues;
    }

}
