package com.igatec.mqlsloth.ci;

import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.ci.constants.ScriptPriority;
import com.igatec.mqlsloth.ci.constants.SlothAdminType;
import com.igatec.mqlsloth.ci.util.CIFullName;
import com.igatec.mqlsloth.script.ModChunk;
import com.igatec.mqlsloth.script.ScriptChunk;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MenuCI extends UIComponentCI {

    private MenuChildren children = new MenuChildren();

    public MenuCI(String name) {
        this(name, CIDiffMode.TARGET);
    }

    public MenuCI(String name, CIDiffMode diffMode) {
        super(SlothAdminType.MENU, name, diffMode);
    }

    public void addChild(String type, String name) {
        children.addItem(type, name);
    }

    public List<Pair<String, String>> getChildren() {
        return children.getItems();
    }

    @Override
    protected void fillDiffCI(AbstractCI newCI, AbstractCI diffCI) {
        super.fillDiffCI(newCI, diffCI);
        MenuCI newCastedCI = (MenuCI) newCI;
        MenuCI diffCastedCI = (MenuCI) diffCI;
        List<Pair<String, String>> oldValues = getChildren();
        List<Pair<String, String>> newValues = newCastedCI.getChildren();
        if (!newValues.equals(oldValues)) {
            oldValues.forEach(item -> diffCastedCI.children.addItemToRemove(item.getLeft(), item.getRight()));
            newValues.forEach(item -> diffCastedCI.children.addItem(item.getLeft(), item.getRight()));
        }

    }

    public boolean isEmpty() {
        if (!super.isEmpty()) {
            return false;
        }
        return children.isEmpty();
    }

    @Override
    public AbstractCI buildDiff(AbstractCI newCI) {
        MenuCI ci = (MenuCI) newCI;
        MenuCI diff = new MenuCI(getName(), CIDiffMode.DIFF);
        fillDiffCI(ci, diff);
        return diff;
    }

    @Override
    public AbstractCI buildDefaultCI() {
        return new MenuCI(getName());
    }

    @Override
    public List<ScriptChunk> buildUpdateScript() {
        List<ScriptChunk> chunks = super.buildUpdateScript();
        CIFullName fName = getCIFullName();

        /* CHILDREN */
        children.getItemsToRemove().forEach(item -> {
            String type = item.getLeft();
            String name = item.getRight();
            chunks.add(new ModChunk(fName, ScriptPriority.SP_AFTER_ADMIN_CREATION_1, M_REMOVE, type, name));
        });
        int priorityCounter = ScriptPriority.SP_AFTER_ADMIN_CREATION_2;
        for (Pair<String, String> item : children.getItems()) {
            String type = item.getLeft();
            String name = item.getRight();
            chunks.add(new ModChunk(fName, priorityCounter++, M_ADD, type, name));
        }

        return chunks;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> fieldsValues = super.toMap();

        fieldsValues.put(Y_CHILDREN, getChildren().stream()
                .map(item -> item.getLeft() + " " + item.getRight())
                .collect(Collectors.toList()));

        return fieldsValues;
    }

    private class MenuChildren {

        private Collection<Pair<String, String>> itemsToRemove = new LinkedList<>();
        private List<Pair<String, String>> items = new LinkedList<>();

        public Collection<Pair<String, String>> getItemsToRemove() {
            return new LinkedList<>(itemsToRemove);
        }

        public void addItemToRemove(String type, String name) {
            itemsToRemove.add(new ImmutablePair<>(type, name));
        }

        public List<Pair<String, String>> getItems() {
            return new LinkedList<>(items);
        }

        public void addItem(String type, String name) {
            items.add(new ImmutablePair<>(type, name));
        }

        public boolean isEmpty() {
            return items.isEmpty() && itemsToRemove.isEmpty();
        }

    }

}
