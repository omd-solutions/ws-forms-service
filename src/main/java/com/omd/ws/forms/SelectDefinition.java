package com.omd.ws.forms;

public class SelectDefinition extends FormFieldDefinition {

    private String displayField;
    private SelectValueProvider valueProvider;

    public SelectDefinition(String fieldName) {
        super(fieldName, ControlType.SELECT);
    }
}
