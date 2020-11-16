package com.omd.ws.forms;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class SelectDefinition extends FormFieldDefinition {

    private String idField;
    private String displayField;
    private SelectValueProvider<?> valueProvider;

    private static final Set<String> ID_ANNOTATION_CLASSES = Set.of("org.springframework.data.annotation.Id");

    private static final Set<String> STANDARD_DISPLAY_FIELDS = Set.of("name", "description", "label");

    public SelectDefinition(Field field, Select annotation) throws EntityConfigurationException {
        super(field, ControlType.SELECT);
        idField = calculateIdField(field, annotation);
        displayField = calculateDisplayField(field, annotation);
        try {
            valueProvider = annotation.valueProvider().getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(String.format("Unable to create an instance of %s", annotation.valueProvider().getName()), e);
        }
    }

    String calculateDisplayField(Field field, Select annotation) throws EntityConfigurationException {
        if(isSimpleType(field)) {
            return null;
        }
        if(!annotation.displayField().isEmpty()) {
            return annotation.displayField();
        }
        Class<?> type = field.getType();
        return Arrays.stream(type.getDeclaredFields()).map(Field::getName)
                .filter(STANDARD_DISPLAY_FIELDS::contains)
                .findFirst()
                .orElseThrow(() -> new EntityConfigurationException(String.format("Error processing field '%s'. Unable to identify a display field for complex bject type '%s'",
                        field.getName(), type.getName())));
    }

    String calculateIdField(Field field, Select annotation) throws EntityConfigurationException {
        if(isSimpleType(field)) {
            return null;
        }
        if(!annotation.idField().isEmpty()) {
            return annotation.idField();
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
        return idField;
    }

    public String getDisplayField() {
        return displayField;
    }

    public List<?> getValues() {
        return valueProvider.getValues();
    }
}
