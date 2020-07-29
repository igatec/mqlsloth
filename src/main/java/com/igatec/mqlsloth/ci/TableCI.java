package com.igatec.mqlsloth.ci;

import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.ci.constants.ScriptPriority;
import com.igatec.mqlsloth.ci.constants.SlothAdminType;
import com.igatec.mqlsloth.ci.util.CIFullName;
import com.igatec.mqlsloth.script.ModChunk;
import com.igatec.mqlsloth.script.ScriptChunk;
import com.igatec.mqlsloth.script.TableCreateChunk;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TableCI extends AdminObjectCI {
    private List<TableColumn> columnList;

    @Getter
    @Setter
    private List<TableColumn> columnsToRemove;

    public TableCI(String name, CIDiffMode diffMode) {
        super(SlothAdminType.WEB_TABLE, name, diffMode);
        if (getDiffMode() == CIDiffMode.TARGET) {
            columnList = new ArrayList<>();
        }
    }

    public TableCI(String name) {
        this(name, CIDiffMode.TARGET);
    }

    public void setColumn(LinkedHashMap columnMap) {
        TableColumn tableColumn = new TableColumn(String.valueOf(columnMap.get("name")));

        if (columnMap.get("label") != null) {
            tableColumn.setLabel(columnMap.get("label").toString());
        }
        if (columnMap.get("businessobject") != null) {
            tableColumn.setBusinessobject(columnMap.get("businessobject").toString());
        }
        if (columnMap.get("relationship") != null) {
            tableColumn.setRelationship(columnMap.get("relationship").toString());
        }
        if (columnMap.get("range") != null) {
            tableColumn.setRange(columnMap.get("range").toString());
        }
        if (columnMap.get("href") != null) {
            tableColumn.setHref(columnMap.get("href").toString());
        }
        if (columnMap.get("settings") != null) {
            tableColumn.setSettings((LinkedHashMap) columnMap.get("settings"));
        }
        columnList.add(tableColumn);
    }

    @Override
    public AbstractCI buildDiff(AbstractCI newCI) {
        TableCI ci = (TableCI) newCI;
        TableCI diff = new TableCI(getName(), CIDiffMode.DIFF);
        fillDiffCI(ci, diff);
        diff.setColumnsToRemove(this.getColumnList());
        return diff;
    }

    @Override
    public List<ScriptChunk> buildUpdateScript() {
//        super.buildUpdateScript();
        List<ScriptChunk> chunks = super.buildUpdateScript();
        CIFullName fName = getCIFullName();

        /* Columns */
        int priority = ScriptPriority.SP_AFTER_ADMIN_CREATION_1;

        for (int i = columnsToRemove.size(); i > 0; i--) {
            chunks.add(new ModChunk(fName, priority++, "column", "delete", String.valueOf(i)));
        }

        for (TableColumn column : columnList) {
            List<String> cmdParamList = new LinkedList<>();
            //cmdParamList.add("system");
            cmdParamList.add("column");
            cmdParamList.add("name");


            cmdParamList.add(String.valueOf(column.getName()));

            if (!column.getLabel().isEmpty()) {
                cmdParamList.add("label");
                cmdParamList.add(String.valueOf(column.getLabel()));
            }
            if (!column.getRange().isEmpty()) {
                cmdParamList.add("range");
                cmdParamList.add(String.valueOf(column.getRange()));
            }
            if (!column.getHref().isEmpty()) {
                cmdParamList.add("href");
                cmdParamList.add(String.valueOf(column.getHref()));
            }
            if (!column.getBusinessobject().isEmpty()) {
                cmdParamList.add("businessobject");
                cmdParamList.add(String.valueOf(column.getBusinessobject()));
            }
            if (!column.getRelationship().isEmpty()) {
                cmdParamList.add("relationship");
                cmdParamList.add(String.valueOf(column.getRelationship()));
            }
            if (column.getSettings() != null) {
                for (String key : column.getSettings().keySet()) {
                    cmdParamList.add("setting");
                    cmdParamList.add(key);
                    cmdParamList.add(column.getSettings().get(key));
                }
            }
            chunks.add(new ModChunk(fName, priority++, cmdParamList.toArray(new String[cmdParamList.size()])));
        }
        return chunks;
    }

    @Override
    protected void fillDiffCI(AbstractCI newCI, AbstractCI diffCI) {
        super.fillDiffCI(newCI, diffCI);
        TableCI newCastedCI = (TableCI) newCI;
        TableCI diffCastedCI = (TableCI) diffCI;
        diffCastedCI.setColumnList(newCastedCI.getColumnList());
        diffCastedCI.setDescription(newCastedCI.getDescription());
        diffCastedCI.setHidden(newCastedCI.isHidden());
    }

    @Override
    public AbstractCI buildDefaultCI() {
        return new TableCI(getName());
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = super.toMap();
        List<Map<String, Object>> columnList = getColumnList().stream().map(this::columnToMap).collect(Collectors.toList());
        map.put(Y_COLUMNS, columnList);
        return map;
    }

    private Map<String, Object> columnToMap(TableColumn tableColumn) {
        Map<String, Object> columnMap = new LinkedHashMap<>();
        columnMap.put(Y_NAME, tableColumn.getName());
        if (!tableColumn.getLabel().isEmpty()) {
            columnMap.put(Y_LABEL, tableColumn.getLabel());
        }
        if (!tableColumn.getBusinessobject().isEmpty()) {
            columnMap.put(Y_BUSINESSOBJECT, tableColumn.getBusinessobject());
        }
        if (!tableColumn.getRelationship().isEmpty()) {
            columnMap.put(Y_RELATIONSHIP, tableColumn.getRelationship());
        }
        if (!tableColumn.getRange().isEmpty()) {
            columnMap.put(Y_RANGE, tableColumn.getRange());
        }
        if (!tableColumn.getHref().isEmpty()) {
            columnMap.put(Y_HREF, tableColumn.getHref());
        }
        if (tableColumn.getSettings() != null && !tableColumn.getSettings().isEmpty()) {
            columnMap.put(Y_SETTINGS, tableColumn.getSettings());
        }
        return columnMap;
    }

    @Override
    protected ScriptChunk buildCreateChunk() {
        return new TableCreateChunk(getCIFullName());
    }

    public TableColumn addColumn() {
        TableColumn newTableColumn = new TableColumn();
        columnList.add(newTableColumn);
        return newTableColumn;
    }

    public List<TableColumn> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<TableColumn> columnList) {
        this.columnList = columnList;
    }
}
