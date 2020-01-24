package com.omd.ws.forms;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static com.omd.ws.forms.Conventions.NO_SECTION;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toList;

public class FormsService {

    EntityFormDefinition generateEntityForm(Class entityClass) {
        String name = entityClass.getName();
        SectionType sectionType = SectionType.PANELS;
        Map<String, SectionDefinition> sections = new HashMap<>();
        EntityForm entityForm = (EntityForm) entityClass.getDeclaredAnnotation(EntityForm.class);
        if (entityForm != null) {
            if (!entityForm.name().isEmpty()) {
                name = entityForm.name();
            }
            sectionType = entityForm.sectionType();
            for (int i = 0; i < entityForm.sections().length; i++) {
                sections.put(entityForm.sections()[i].name(), new SectionDefinition(i, entityForm.sections()[i].caption()));
            }
        }
        if (sections.isEmpty()) {
            sections.put(NO_SECTION, new SectionDefinition(0, NO_SECTION));
        }
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            addFormField(field, sections);
        }

        return new EntityFormDefinition(name, sectionType, sections.values().stream()
                .sorted(comparingInt(SectionDefinition::getOrderIndex))
                .collect(toList()));
    }

    void addFormField(Field field, Map<String, SectionDefinition> sections) {
        Annotation fieldAnnotation = getFieldAnnotation(field);
        ControlType controlType = ControlType.getControlType(field);
        if(fieldAnnotation != null) {
            controlType = ControlType.getControlType(fieldAnnotation);
        }
        if(controlType == null) {
            throw new RuntimeException(String.format("Unable to infer a control type for field %s", field.getName()));
        }
        FormField formField = field.getAnnotation(FormField.class);
        FormFieldDefinition definition = new FormFieldDefinition(field.getName());
        definition.setCaption(getFieldCaption(field, formField));
        definition.setColumns(formField == null ? 12 : formField.columns());
        definition.setControlType(controlType);
        String section = formField == null ? NO_SECTION : formField.section();
        SectionDefinition sectionDefinition = sections.get(section);
        if(sectionDefinition == null) {
            if(section.equals(NO_SECTION)) {
                throw new RuntimeException(String.format("Unable to place field %s, you must specify a section for each field if the entity defines sections", field.getName()));
            } else {
                throw new RuntimeException(String.format("Unable to place field %s, the specified section %s does not exist on this entity", field.getName(), section));
            }
        }
        sectionDefinition.addField(definition);
    }

    String getFieldCaption(Field field, FormField formField) {
        if(formField != null && !formField.caption().isEmpty()) {
            return formField.caption();
        }
        String [] words = field.getName().split("[A-Z]");
        String withSpaces = String.join(" ", words);
        return withSpaces.substring(0, 1).toUpperCase() + withSpaces.substring(1);
    }
}
