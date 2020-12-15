package com.omd.ws.forms;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public abstract class PojoFieldDefinition extends FormFieldDefinition {

    private String idFieldName;
    private Field idField;
    private String displayFieldName;
    private Field displayField;

    private static final Set<String> ID_ANNOTATION_CLASSES = Set.of("org.springframework.data.annotation.Id");
    private static final Set<String> STANDARD_DISPLAY_FIELDS = Set.of("name", "description", "label");

    public PojoFieldDefinition(Field field, ControlType controlType,
                               String idField, String displayField) throws EntityConfigurationException {
        super(field, controlType);
        this.idFieldName = calculateIdField(field, idField);
        this.idField = getPojoField(idFieldName);
        this.displayFieldName = calculateDisplayField(field, displayField);
        this.displayField = getPojoField(displayFieldName);
    }

    Field getPojoField(String fieldName) throws EntityConfigurationException {
        if(fieldName != null) {
            try {
                Field field = getFieldType().getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException e) {
                throw new EntityConfigurationException(String.format("The field %s is annotated as having a pojo field called %s but this field does not exist on the type %s",
                        getFieldName(), fieldName, getFieldType().getName()), e);
            }
        }
        return null;
    }

    String calculateDisplayField(Field field, String displayField) throws EntityConfigurationException {
        if(isSimpleType()) {
            return null;
        }
        if(!displayField.isEmpty()) {
            return displayField;
        }
        Class<?> type = field.getType();
        return Arrays.stream(type.getDeclaredFields()).map(Field::getName)
                .filter(STANDARD_DISPLAY_FIELDS::contains)
                .findFirst()
                .orElseThrow(() -> new EntityConfigurationException(String.format("Error processing field '%s'. Unable to identify a display field for complex bject type '%s'",
                        field.getName(), type.getName())));
    }

    String calculateIdField(Field field, String idField) throws EntityConfigurationException {
        if(isSimpleType()) {
            return null;
        }
        if(!idField.isEmpty()) {
            return idField;
        }
        Class<?> type = field.getType();
        //e.g. Employee would be employeeId
        String typeIdFieldName = type.getSimpleName().substring(0, 1).toLowerCase() + type.getSimpleName().substring(1) + "Id";
        Set<String> potentialIdFields = Set.of("id", typeIdFieldName);
        String secondChoice = null;
        for(Field typeField : type.getDeclaredFields()) {
            Optional<String> idAnnotation = Arrays.stream(typeField.getAnnotations())
                    .map(a -> a.annotationType().getName())
                    .filter(ID_ANNOTATION_CLASSES::contains)
                    .findFirst();
            if(idAnnotation.isPresent()) {
                return typeField.getName();
            }
            if(potentialIdFields.contains(typeField.getName())) {
                secondChoice = typeField.getName();
            }
        }
        if(secondChoice != null) {
            return secondChoice;
        } else {
            throw new EntityConfigurationException(
                    String.format("Error processing select field '%s'. Unable to identify an id field for complex object type '%s'",
                            field.getName(), type.getName()));
        }
    }

    public String getIdField() {
        return idFieldName;
    }

    public String getDisplayField() {
        return displayFieldName;
    }

    protected boolean isPojoField() {
        return idFieldName != null;
    }

    protected Object findValue(List<?> values, String idValue) {
        if (values == null) {
            return null;
        }
        return values.stream()
                .filter(v -> {
                    if (isPojoField()) {
                        return idValue.equals(getFieldValueAsString("id", idField, v));
                    } else {
                        return wrangleValue(idValue).equals(v);
                    }
                })
                .findFirst()
                .orElse(null);
    }

    String getFieldValueAsString(String fieldName, Field field, Object pojo) {
        if(field == null) {
            throw new RuntimeException(String.format("%s field for %s is not set", fieldName, getFieldName()));
        }
        try {
            return field.get(pojo).toString();
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unexpected access exception while reading id field value", e);
        }
    }

}
