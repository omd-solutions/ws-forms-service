package com.omd.ws.forms;

import java.util.ArrayList;
import java.util.List;

public class PanelDefinition {

    private int orderIndex;
    private String caption;
    private List<FormFieldDefinition> fields = new ArrayList<>();

    PanelDefinition(int orderIndex, String caption) {
        this.orderIndex = orderIndex;
        this.caption = caption;
    }

    int getOrderIndex() {
        return orderIndex;
    }

    public String getCaption() {
        return caption;
    }

    public List<FormFieldDefinition> getFields() {
        return fields;
    }

    void addField(FormFieldDefinition field) {
        fields.add(field);
    }
}
