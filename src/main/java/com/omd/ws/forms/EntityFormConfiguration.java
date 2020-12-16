package com.omd.ws.forms;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static com.omd.ws.forms.Conventions.NO_PANEL;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toList;

class EntityFormConfiguration {

    private String entityName;
    private final EntityFormDefinition formDefinition;
    private final Map<String, FormFieldDefinition> fieldDefinitions = new HashMap<>();

    private static final Set<Class<?>> FIELD_ANNOTATION_CLASSES = new HashSet<>(Arrays.asList(
            TextEditor.class,
            Select.class,
            FilteredSelect.class
    ));

    EntityFormConfiguration(Class<?> entityClass) throws EntityConfigurationException {
        entityName = entityClass.getName();
        Map<String, PanelDefinition> panels = new HashMap<>();
        EntityForm entityForm = entityClass.getDeclaredAnnotation(EntityForm.class);
        if (entityForm != null) {
            if (!entityForm.name().isEmpty()) {
                entityName = entityForm.name();
            }
            for (int i = 0; i < entityForm.panels().length; i++) {
                panels.put(entityForm.panels()[i].name(), new PanelDefinition(i, entityForm.panels()[i].caption()));
            }
        }
        if (panels.isEmpty()) {
            panels.put(NO_PANEL, new PanelDefinition(0, NO_PANEL));
        }
        Field[] fields = entityClass.getDeclaredFields();
        Set<String> filterFields = new HashSet<>();
        for (Field field : fields) {
            FormsIgnore ignoreAnnotation = field.getAnnotation(FormsIgnore.class);
            if (ignoreAnnotation == null) {
                FormFieldDefinition fieldDefinition = createFormFieldDefinition(field);
                addFormField(fieldDefinition, panels);
                fieldDefinitions.put(fieldDefinition.getFieldName(), fieldDefinition);
                if(fieldDefinition instanceof FilteredSelectDefinition) {
                    String filteredBy = ((FilteredSelectDefinition) fieldDefinition).getFilteredBy();
                    filterFields.add(filteredBy);
                }
            }
        }

        formDefinition = new EntityFormDefinition(entityName, panels.values().stream()
                .sorted(comparingInt(PanelDefinition::getOrderIndex))
                .collect(toList()), filterFields);
    }

    void addFormField(FormFieldDefinition fieldDefinition, Map<String, PanelDefinition> sections) throws EntityConfigurationException {
        String section = fieldDefinition.getSection();
        PanelDefinition panelDefinition = sections.get(section);
        if (panelDefinition == null) {
            if (section.equals(NO_PANEL)) {
                throw new EntityConfigurationException(String.format("Unable to place field %s, you must specify a section for each field if the entity defines sections", fieldDefinition.getFieldName()));
            } else {
                throw new EntityConfigurationException(String.format("Unable to place field %s, the specified section %s does not exist on this entity", fieldDefinition.getFieldName(), section));
            }
        }
        panelDefinition.addField(fieldDefinition);
    }

    FormFieldDefinition createFormFieldDefinition(Field field) throws EntityConfigurationException {
        Annotation fieldAnnotation = getFieldAnnotation(field);
        if (fieldAnnotation != null) {
            if (fieldAnnotation instanceof TextEditor) {
                TextEditor textEditor = (TextEditor) fieldAnnotation;
                return new FormFieldDefinition(field, textEditor.html() ? ControlType.HTML_EDITOR : ControlType.TEXT_AREA);
            }
            if (fieldAnnotation instanceof Select) {
                return new SelectDefinition(field, (Select) fieldAnnotation);
            }
            if (fieldAnnotation instanceof FilteredSelect) {
                return new FilteredSelectDefinition(field, (FilteredSelect) fieldAnnotation);
            }
        }
        return new FormFieldDefinition(field);
    }

    Annotation getFieldAnnotation(Field field) {
        List<Annotation> annotations = Arrays.stream(field.getAnnotations())
                .filter(a -> FIELD_ANNOTATION_CLASSES.contains(a.annotationType()))
                .collect(toList());
        if (annotations.size() > 1) {
            throw new RuntimeException(String.format("The field %s should only have one of the following %s", field.getName(),
                    annotations.stream().map(a -> a.annotationType().getSimpleName()).collect(Collectors.joining(", "))));
        }
        return annotations.isEmpty() ? null : annotations.get(0);
    }

    String getEntityName() {
        return entityName;
    }

    EntityFormDefinition getFormDefinition() {
        return formDefinition;
    }

    <T extends FormFieldDefinition> List<T> getFieldDefinitions(Class<T> formFieldClass) {
        return fieldDefinitions.values().stream()
                .filter(f -> f.getClass() == formFieldClass)
                .map(f -> ((T) f))
                .collect(toList());
    }

    FormFieldDefinition getFieldDefinition(String fieldName) {
        return fieldDefinitions.get(fieldName);
    }
}
