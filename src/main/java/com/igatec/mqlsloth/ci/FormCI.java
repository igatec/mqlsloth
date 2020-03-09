package com.igatec.mqlsloth.ci;

import com.igatec.mqlsloth.ci.constants.CIDiffMode;
import com.igatec.mqlsloth.ci.constants.ScriptPriority;
import com.igatec.mqlsloth.ci.constants.SlothAdminType;
import com.igatec.mqlsloth.ci.util.CIFullName;
import com.igatec.mqlsloth.script.FormCreateChunk;
import com.igatec.mqlsloth.script.ModChunk;
import com.igatec.mqlsloth.script.ScriptChunk;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

public class FormCI extends AdminObjectCI {
    private List<FormField> fields;

    @Getter
    @Setter
    private List<FormField> fieldsToRemove;

    public FormCI(String name) {
        this(name, CIDiffMode.TARGET);
    }

    public FormCI(String name, CIDiffMode diffMode) {
        super(SlothAdminType.FORM, name, diffMode);
        if (getDiffMode() == CIDiffMode.TARGET) {
            fields = new ArrayList<>();
        }
    }

    public void setField(LinkedHashMap fieldMap) {
        FormField formField = new FormField(String.valueOf(fieldMap.get("name")));

        if (fieldMap.get("label") != null) {
            formField.setLabel(fieldMap.get("label").toString());
        }
        if (fieldMap.get("businessobject") != null) {
            formField.setBusinessobject(fieldMap.get("businessobject").toString());
        }
        if (fieldMap.get("relationship") != null) {
            formField.setRelationship(fieldMap.get("relationship").toString());
        }
        if (fieldMap.get("range") != null) {
            formField.setRange(fieldMap.get("range").toString());
        }
        if (fieldMap.get("href") != null) {
            formField.setHref(fieldMap.get("href") .toString());
        }
        if (fieldMap.get("settings")  != null) {
            formField.setSettings((LinkedHashMap)fieldMap.get("settings"));
        }
        fields.add(formField);
    }

    private List<FormField> getFields() {
        return fields;
    }

    public void setFields(List<FormField> fields) {
        this.fields = fields;
    }

    @Override
    public AbstractCI buildDiff(AbstractCI newCI) {
        FormCI ci = (FormCI) newCI;
        FormCI diff = new FormCI(getName(), CIDiffMode.DIFF);
        fillDiffCI(ci, diff);
        diff.setFieldsToRemove(this.getFields());
        return diff;
    }

    @Override
    public List<ScriptChunk> buildUpdateScript() {
        super.buildUpdateScript();
        List<ScriptChunk> chunks = super.buildUpdateScript();
        CIFullName fName = getCIFullName();

        /* FIELDS */
        int priority = ScriptPriority.SP_AFTER_ADMIN_CREATION_1;

        for (int i = fieldsToRemove.size(); i > 0; i--) {
            chunks.add(new ModChunk(fName, priority++, "field", "delete", String.valueOf(i)));
        }

        for (FormField field : fields) {
            List<String> fieldList = new LinkedList<>();
            fieldList.add("field");
            fieldList.add("name");
            fieldList.add(String.valueOf(field.getName()));

            if (!field.getLabel().isEmpty()) {
                fieldList.add("label");
                fieldList.add(String.valueOf(field.getLabel()));
            }
            if (!field.getRange().isEmpty()) {
                fieldList.add("range");
                fieldList.add(String.valueOf(field.getRange()));
            }
            if (!field.getHref().isEmpty()) {
                fieldList.add("href");
                fieldList.add(String.valueOf(field.getHref()));
            }
            if (!field.getBusinessobject().isEmpty()) {
                fieldList.add("businessobject");
                fieldList.add(String.valueOf(field.getBusinessobject()));
            }
            if (!field.getRelationship().isEmpty()) {
                fieldList.add("relationship");
                fieldList.add(String.valueOf(field.getRelationship()));
            }
            if (field.getSettings() != null) {
                for (String key : field.getSettings().keySet()) {
                    fieldList.add("setting");
                    fieldList.add(key);
                    fieldList.add(field.getSettings().get(key));
                }
            }
            chunks.add(new ModChunk(fName, priority++, fieldList.toArray(new String[fieldList.size()])));
        }
        return chunks;
    }

    @Override
    protected void fillDiffCI(AbstractCI newCI, AbstractCI diffCI) {
        super.fillDiffCI(newCI, diffCI);
        FormCI newCastedCI = (FormCI) newCI;
        FormCI diffCastedCI = (FormCI) diffCI;
        diffCastedCI.setFields(newCastedCI.getFields());
        diffCastedCI.setDescription(newCastedCI.getDescription());
        diffCastedCI.setHidden(newCastedCI.isHidden());
    }

    @Override
    public AbstractCI buildDefaultCI() {
        return new FormCI(getName());
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = super.toMap();
        List<Map<String, Object>> fieldList = getFields().stream().map(this::fieldToMap).collect(Collectors.toList());
        map.put(Y_FIELDS, fieldList);
        return map;
    }

    private Map<String, Object> fieldToMap(FormField formField) {
        Map<String, Object> fieldMap = new LinkedHashMap<>();
        fieldMap.put(Y_NAME, formField.getName());
        if (!formField.getLabel().isEmpty()) {
            fieldMap.put(Y_LABEL, formField.getLabel());
        }
        if (!formField.getBusinessobject().isEmpty()) {
            fieldMap.put(Y_BUSINESSOBJECT, formField.getBusinessobject());
        }
        if (!formField.getRelationship().isEmpty()) {
            fieldMap.put(Y_RELATIONSHIP, formField.getRelationship());
        }
        if (!formField.getRange().isEmpty()) {
            fieldMap.put(Y_RANGE, formField.getRange());
        }
        if (!formField.getHref().isEmpty()) {
            fieldMap.put(Y_HREF, formField.getHref());
        }
        if (formField.getSettings() != null && !formField.getSettings().isEmpty()) {
            fieldMap.put(Y_SETTINGS, formField.getSettings());
        }
        return fieldMap;
    }

    @Override
    protected ScriptChunk buildCreateChunk() {
        return new FormCreateChunk(getCIFullName());
    }

    public FormField addField() {
        FormField newFormField = new FormField();
        fields.add(newFormField);
        return newFormField;
    }
}
