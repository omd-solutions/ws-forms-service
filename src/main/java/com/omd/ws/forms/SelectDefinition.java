package com.omd.ws.forms;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

public class SelectDefinition extends FormFieldDefinition {

    private String idField;
    private String displayField;
    private SelectValueProvider<?> valueProvider;

    public SelectDefinition(Field field, Select annotation) throws EntityConfigurationException {
        super(field, ControlType.SELECT);
        idField = calculateIdField(field, annotation);
        displayField = annotation.displayField();
        try {
            valueProvider = annotation.valueProvider().getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(String.format("Unable to create an instance of %s", annotation.valueProvider().getName()), e);
        }
    }

    String calculateIdField(Field field, Select annotation) {
        if(isSimpleType(field)) {
            return null;
        }
        if(!annotation.idField().isEmpty()) {
            return annotation.idField();
        }
        Class<?> type = field.getType();
        //e.g. Employee would be employeeId
        String typeIdFieldName = type.getSimpleName().substring(0, 1).toLowerCase() + type.getSimpleName().substring(1) + "Id"
        Set<String> potentialIdFields = Set.of("id", typeIdFieldName);
        for(Field typeField : type.)
    }

    public String getDisplayField() {
        return displayField;
    }

    public List<?> getValues() {
        return valueProvider.getValues();
    }
}
