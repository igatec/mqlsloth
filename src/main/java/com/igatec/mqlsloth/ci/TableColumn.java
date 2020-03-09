package com.igatec.mqlsloth.ci;

import java.util.HashMap;
import java.util.Map;

public class TableColumn {
    private String name;
    private String label;
    private String businessobject;
    private String relationship;
    private String range;
    private String href;
    private Map<String, String> settings;

    public TableColumn(String name) {
        this.name = name;
        label = "";
        businessobject = "";
        relationship = "";
        range = "";
        href = "";
        settings = new HashMap<>();
    }

    public TableColumn() {
        this.name = "";
        this.label = "";
        this.businessobject = "";
        this.relationship = "";
        this.range = "";
        this.href = "";
        this.settings = new HashMap<>();
    }

    public void setSetting(String key, String value) {
        settings.put(key, value);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getBusinessobject() {
        return businessobject;
    }

    public void setBusinessobject(String businessobject) {
        this.businessobject = businessobject;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public Map<String, String> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, String> settings) {
        this.settings = settings;
    }
}
