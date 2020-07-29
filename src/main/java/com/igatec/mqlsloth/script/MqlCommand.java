package com.igatec.mqlsloth.script;

import java.util.Arrays;
import java.util.LinkedList;

public class MqlCommand implements MqlAction {
    private static final int SYSTEM_TABLE_KEY_INDEX = 3;
    private String[] head = null;
    private String[][] params = null;

    public MqlCommand(String[] headCmd) {
        head = Arrays.copyOf(headCmd, headCmd.length);
    }

    public String[] getHead() {
        return head;
    }

    public String[][] getParams() {
        if (params == null) {
            return new String[0][];
        }
        return params;
    }

    public void setParams(String[][] params) {
        this.params = params;
    }

    public String[] plainify() {
        //TODO need checking fo table object and add system keyword example:
        // "modify","table", "MyTab", "system", "nothidden", "description", "hui"
        LinkedList<String> result = new LinkedList<>(Arrays.asList(getHead()));
        for (String[] ss : getParams()) {
            result.addAll(Arrays.asList(ss));
        }
        if (result.get(1).equals("table")) {
            result.add(SYSTEM_TABLE_KEY_INDEX, "system");
        }
        return result.toArray(new String[0]);
    }

    @Override
    public String toString() {
        return Arrays.toString(plainify());
    }
}
