package com.omd.ws.forms;

import java.lang.reflect.Field;
import java.util.List;

public class SelectDefinition extends PojoFieldDefinition {

    private SelectValueProvider<?> valueProvider;

    public SelectDefinition(Field field, Select annotation) throws EntityConfigurationException {
        super(field, ControlType.SELECT, annotation.idField(), annotation.displayField());
        try {
            valueProvider = annotation.valueProvider().getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(String.format("Unable to create an instance of %s", annotation.valueProvider().getName()), e);
        }
    }

    public List<?> getValues() {
        return valueProvider.getValues();
    }

    public Object getValue(String idValue) {
        if(isPojoField()) {
            return getValues().stream().filter(v -> idValue.equals(getIdFieldValueAsString(v))).findFirst().orElse(null);
        } else {
            return wrangleValue(idValue);
        }
    }
}
