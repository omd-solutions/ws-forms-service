package com.omd.ws.forms;

import org.springframework.stereotype.Service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static com.omd.ws.forms.Conventions.NO_SECTION;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toList;

@Service
public class SimpleFormsService implements FormsService {

    private static final Set<Class> FIELD_ANNOTATION_CLASSES = new HashSet<>(Arrays.asList(
            TextEditor.class,
            Select.class,
            FilteredSelect.class
    ));

    private Map<String, EntityFormDefinition> definitions = new HashMap<>();

    @Override
    public void registerEntityForm(Class<?> entityClass) throws EntityConfigurationException {
        EntityFormDefinition definition = generateEntityForm(entityClass);
        definitions.put(definition.getName(), generateEntityForm(entityClass));
    }

    @Override
    public EntityFormDefinition getEntityFormDefinition(String entityName) {
        return definitions.get(entityName);
    }

    EntityFormDefinition generateEntityForm(Class<?> entityClass) throws EntityConfigurationException {
        String name = entityClass.getName();
        SectionType sectionType = SectionType.PANELS;
        Map<String, SectionDefinition> sections = new HashMap<>();
        EntityForm entityForm = entityClass.getDeclaredAnnotation(EntityForm.class);
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

    void addFormField(Field field, Map<String, SectionDefinition> sections) throws EntityConfigurationException {
        FormFieldDefinition definition = createFormFieldDefinition(field);
        String section = definition.getSection();
        SectionDefinition sectionDefinition = sections.get(section);
        if(sectionDefinition == null) {
            if(section.equals(NO_SECTION)) {
                throw new EntityConfigurationException(String.format("Unable to place field %s, you must specify a section for each field if the entity defines sections", field.getName()));
            } else {
                throw new EntityConfigurationException(String.format("Unable to place field %s, the specified section %s does not exist on this entity", field.getName(), section));
            }
        }
        sectionDefinition.addField(definition);
    }

    FormFieldDefinition createFormFieldDefinition(Field field) throws EntityConfigurationException {
        Annotation fieldAnnotation = getFieldAnnotation(field);
        if(fieldAnnotation != null) {
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
        if(annotations.size() > 1) {
            throw new RuntimeException(String.format("The field %s should only have one of the following %s", field.getName(),
                    annotations.stream().map(a -> a.annotationType().getSimpleName()).collect(Collectors.joining(", "))));
        }
        return annotations.isEmpty() ? null : annotations.get(0);
    }
}
