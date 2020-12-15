package com.omd.ws.forms;

import java.lang.reflect.Field;
import java.util.List;

public class FilteredSelectDefinition extends PojoFieldDefinition {

    private FilteredSelectValueProvider valueProvider;
    private String filteredBy;

    public FilteredSelectDefinition(Field field, FilteredSelect annotation) throws EntityConfigurationException {
        super(field, ControlType.SELECT, annotation.idField(), annotation.displayField());
        try {
            valueProvider = annotation.valueProvider().getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(String.format("Unable to create an instance of %s", annotation.valueProvider().getName()), e);
        }
        filteredBy = annotation.filteredBy();
    }

    public String getFilteredBy() {
        return filteredBy;
    }

    public List<?> getValues(Object filter) {
        return valueProvider.getValues(filter);
    }

    public Object getValue(String idValue, Object filter) {
        List<?> values = getValues(filter);
        return findValue(values, idValue);
    }
}
