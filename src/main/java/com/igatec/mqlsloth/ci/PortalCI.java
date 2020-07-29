package com.igatec.mqlsloth.ci;

import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.ci.constants.ScriptPriority;
import com.igatec.mqlsloth.ci.constants.SlothAdminType;
import com.igatec.mqlsloth.ci.util.CIFullName;
import com.igatec.mqlsloth.script.ModChunk;
import com.igatec.mqlsloth.script.ScriptChunk;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PortalCI extends UIComponentCI {

    private PortalChannels channels = new PortalChannels();

    public PortalCI(String name) {
        this(name, CIDiffMode.TARGET);
    }

    public PortalCI(String name, CIDiffMode diffMode) {
        super(SlothAdminType.PORTAL, name, diffMode);
    }

    public void addChannel(String channel, boolean newRow) {
        channels.addItem(channel, newRow);
    }

    public List<List<String>> getChannels() {
        return channels.getItems();
    }

    @Override
    protected void fillDiffCI(AbstractCI newCI, AbstractCI diffCI) {
        super.fillDiffCI(newCI, diffCI);
        PortalCI newCastedCI = (PortalCI) newCI;
        PortalCI diffCastedCI = (PortalCI) diffCI;
        List<List<String>> oldValues = getChannels();
        List<List<String>> newValues = newCastedCI.getChannels();
        if (!newValues.equals(oldValues)) {
            oldValues.forEach(subList -> subList.forEach(item -> {
                diffCastedCI.channels.addItemToRemove(item);
            }));
            newValues.forEach(subList -> {
                for (int i = 0; i < subList.size(); i++) {
                    boolean newRow = i == 0;
                    diffCastedCI.addChannel(subList.get(i), newRow);
                }
            });
        }
    }

    public boolean isEmpty() {
        if (!super.isEmpty()) {
            return false;
        }
        return channels.isEmpty();
    }

    @Override
    public AbstractCI buildDiff(AbstractCI newCI) {
        PortalCI ci = (PortalCI) newCI;
        PortalCI diff = new PortalCI(getName(), CIDiffMode.DIFF);
        fillDiffCI(ci, diff);
        return diff;
    }

    @Override
    public AbstractCI buildDefaultCI() {
        return new PortalCI(getName());
    }

    @Override
    public List<ScriptChunk> buildUpdateScript() {
        List<ScriptChunk> chunks = super.buildUpdateScript();
        CIFullName fName = getCIFullName();

        /* CHILDREN */
        int priority = ScriptPriority.SP_AFTER_ADMIN_CREATION_1;
        for (String item : channels.getItemsToRemove()) {
            chunks.add(new ModChunk(fName, priority++, M_REMOVE, M_CHANNEL, item));
        }
        for (List<String> subList : channels.getItems()) {
            for (int i = 0; i < subList.size(); i++) {
                String item = subList.get(i);
                chunks.add(new ModChunk(fName, priority++,
                        M_PLACE, item, (i == 0) ? M_NEWROW : StringUtils.EMPTY, M_AFTER, null
                )); // Null will be processed as empty string
            }
        }

        return chunks;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> fieldsValues = super.toMap();

        fieldsValues.put(Y_CHANNELS, getChannels());

        return fieldsValues;
    }

    private class PortalChannels {

        private Collection<String> itemsToRemove = new LinkedList<>();
        private List<LinkedList<String>> items = new LinkedList<>();

        public Collection<String> getItemsToRemove() {
            return new LinkedList<>(itemsToRemove);
        }

        public void addItemToRemove(String item) {
            itemsToRemove.add(item);
        }

        public List<List<String>> getItems() {
            List<List<String>> result = new LinkedList<>();
            items.forEach(subList -> result.add((List<String>) subList.clone()));
            return result;
        }

        public void addItem(String item, boolean newRow) {
            LinkedList<String> subList;
            if (items.size() == 0 || newRow) {
                subList = new LinkedList<>();
                items.add(subList);
            } else {
                subList = items.get(items.size() - 1);
            }
            subList.add(item);
        }

        public boolean isEmpty() {
            return items.isEmpty() && itemsToRemove.isEmpty();
        }
    }
}
