package com.omd.ws.forms;

import java.lang.reflect.Field;

public class FilteredSelectDefinition extends FormFieldDefinition {

    private String displayField;
    private FilteredSelectValueProvider valueProvider;
    private String filteredBy;

    public FilteredSelectDefinition(Field field, FilteredSelect annotation) throws EntityConfigurationException {
        super(field, ControlType.FILTERED_SELECT);
    }
}
