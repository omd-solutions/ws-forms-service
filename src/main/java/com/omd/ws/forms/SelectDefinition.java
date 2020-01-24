package com.omd.ws.forms;

import java.lang.reflect.Field;
import java.util.List;

public class SelectDefinition extends FormFieldDefinition {

    private String displayField;
    private SelectValueProvider<?> valueProvider;

    public SelectDefinition(Field field, Select annotation) throws EntityConfigurationException {
        super(field, ControlType.SELECT);
        displayField = annotation.displayField();
        try {
            valueProvider = annotation.valueProvider().getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(String.format("Unable to create an instance of %s", annotation.valueProvider().getName()), e);
        }
    }

    public String getDisplayField() {
        return displayField;
    }

    public List<?> getValues() {
        return valueProvider.getValues();
    }
}
