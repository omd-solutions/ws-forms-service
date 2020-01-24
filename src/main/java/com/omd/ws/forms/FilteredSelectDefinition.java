package com.omd.ws.forms;

public class FilteredSelectDefinition extends FormFieldDefinition {

    private String displayField;
    private FilteredSelectValueProvider valueProvider;
    private String filteredBy;

    public FilteredSelectDefinition(String fieldName) {
        super(fieldName, ControlType.FILTERED_SELECT);
    }
}
