package com.omd.ws.forms;

public class FormFieldDefinition {

    private String fieldName;
    private String caption;
    private int columns;
    private ControlType controlType;

    public FormFieldDefinition(String fieldName, ControlType controlType) {
        this.fieldName = fieldName;
        this.controlType = controlType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getCaption() {
        return caption;
    }

    void setCaption(String caption) {
        this.caption = caption;
    }

    public int getColumns() {
        return columns;
    }

    void setColumns(int columns) {
        this.columns = columns;
    }

    public ControlType getControlType() {
        return controlType;
    }
}
