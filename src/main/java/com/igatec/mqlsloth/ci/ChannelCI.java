package com.igatec.mqlsloth.ci;

import com.igatec.mqlsloth.ci.annotation.ModStringProvider;
import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.ci.constants.ScriptPriority;
import com.igatec.mqlsloth.ci.constants.SlothAdminType;
import com.igatec.mqlsloth.ci.util.CIFullName;
import com.igatec.mqlsloth.script.ModChunk;
import com.igatec.mqlsloth.script.ScriptChunk;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ChannelCI extends UIComponentCI {

    private String height;
    private CommandList commands = new CommandList();

    public ChannelCI(String name) {
        this(name, CIDiffMode.TARGET);
    }

    public ChannelCI(String name, CIDiffMode diffMode) {
        super(SlothAdminType.CHANNEL, name, diffMode);
        if (getDiffMode() == CIDiffMode.TARGET) {
            initTarget();
        } else {
            initDiff();
        }
    }

    private void initDiff() {
        height = null;
    }

    private void initTarget() {
        height = "0";
    }

    @ModStringProvider(value = M_HEIGHT)
    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void addCommand(String value) {
        commands.addItem(value);
    }

    public List<String> getCommands() {
        return commands.getItems();
    }

    @Override
    protected void fillDiffCI(AbstractCI newCI, AbstractCI diffCI) {
        super.fillDiffCI(newCI, diffCI);
        ChannelCI newCastedCI = (ChannelCI) newCI;
        ChannelCI diffCastedCI = (ChannelCI) diffCI;

        String height = newCastedCI.getHeight();
        if (height != null && !height.equals(getHeight())) {
            diffCastedCI.setHeight(height);
        }

        List<String> oldValues = getCommands();
        List<String> newValues = newCastedCI.getCommands();
        if (!newValues.equals(oldValues)) {
            oldValues.forEach(item -> diffCastedCI.commands.addItemToRemove(item));
            newValues.forEach(item -> diffCastedCI.commands.addItem(item));
        }
    }

    @Override
    public List<ScriptChunk> buildUpdateScript() {
        List<ScriptChunk> chunks = super.buildUpdateScript();
        CIFullName fName = getCIFullName();

        /* COMMANDS */
        commands.getItemsToRemove().forEach(item -> {
            chunks.add(new ModChunk(fName, ScriptPriority.SP_AFTER_ADMIN_CREATION_1, M_REMOVE, M_COMMAND, item));
        });
        int priorityCounter = ScriptPriority.SP_AFTER_ADMIN_CREATION_2;
        for (String item : commands.getItems()) {
            chunks.add(new ModChunk(fName, priorityCounter++, M_PLACE, item, M_BEFORE, null)); // null instead of empty string
        }

        return chunks;
    }

    public boolean isEmpty() {
        if (!super.isEmpty()) {
            return false;
        }
        return commands.isEmpty() && height == null;
    }

    @Override
    public AbstractCI buildDiff(AbstractCI newCI) {
        ChannelCI ci = (ChannelCI) newCI;
        ChannelCI diff = new ChannelCI(getName(), CIDiffMode.DIFF);
        fillDiffCI(ci, diff);
        return diff;
    }

    @Override
    public AbstractCI buildDefaultCI() {
        return new ChannelCI(getName());
    }

    public Map<String, Object> toMap() {
        Map<String, Object> fieldsValues = super.toMap();

        fieldsValues.put(Y_HEIGHT, getHeight());
        fieldsValues.put(Y_COMMANDS, getCommands());

        return fieldsValues;
    }

    private class CommandList {

        private Collection<String> itemsToRemove = new LinkedList<>();
        private List<String> items = new LinkedList<>();

        public Collection<String> getItemsToRemove() {
            return new LinkedList<>(itemsToRemove);
        }

        public void addItemToRemove(String item) {
            itemsToRemove.add(item);
        }

        public List<String> getItems() {
            return new LinkedList<>(items);
        }

        public void addItem(String item) {
            items.add(item);
        }

        public boolean isEmpty() {
            return items.isEmpty() && itemsToRemove.isEmpty();
        }

    }

}
