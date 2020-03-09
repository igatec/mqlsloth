package com.igatec.mqlsloth.ci;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class FormField {
    private String name;
    private String label;
    private String businessobject;
    private String relationship;
    private String range;
    private String href;
    private Map<String, String> settings;

    public FormField(String name) {
        this.name = name;
        label = "";
        businessobject = "";
        relationship = "";
        range = "";
        href = "";
        settings = new HashMap<>();
    }

    public FormField() {
        this.name = "";
        this.label = "";
        this.businessobject = "";
        this.relationship = "";
        this.range = "";
        this.href = "";
        this.settings = new HashMap<>();
    }
}
