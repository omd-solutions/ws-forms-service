package com.omd.ws.forms;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.omd.ws.forms.ControlType.*;
import static java.util.stream.Collectors.toList;

public class FormFieldDefinitionFactory {

    private static final Set<Class> NUMBER_CLASSES = new HashSet<>(Arrays.asList(
            int.class,
            Integer.class,
            double.class,
            Double.class,
            float.class,
            Float.class,
            short.class,
            Short.class
    ));

    private static final Set<Class> DATE_TIME_CLASSES = new HashSet<>(Arrays.asList(
            Date.class,
            LocalDateTime.class,
            ZonedDateTime.class
    ));

    private static final Set<Class> BOOLEAN_CLASSES = new HashSet<>(Arrays.asList(
            Boolean.class,
            boolean.class
    ));

    private static final Set<Class> FIELD_ANNOTATION_CLASSES = new HashSet<>(Arrays.asList(
            TextEditor.class,
            Select.class,
            FilteredSelect.class
    ));

    static Annotation getFieldAnnotation(Field field) {
        List<Annotation> annotations = Arrays.stream(field.getAnnotations())
                .filter(a -> FIELD_ANNOTATION_CLASSES.contains(a.annotationType()))
                .collect(toList());
        if(annotations.size() > 1) {
            throw new RuntimeException(String.format("The field %s should only have one of the following %s", field.getName(),
                    annotations.stream().map(a -> a.annotationType().getSimpleName()).collect(Collectors.joining(", "))));
        }
        return annotations.get(0);
    }

    static FormFieldDefinition createFormFieldDefinition(Field field) {
        FormFieldDefinition result;
        Class type = field.getType();
        if (type == String.class) {
            result = new FormFieldDefinition(field.getName(), TEXT);
        }
        if (NUMBER_CLASSES.contains(type)) {
            result = new FormFieldDefinition(field.getName(), NUMBER);
        }
        if (DATE_TIME_CLASSES.contains(type)) {
            result = new FormFieldDefinition(field.getName(), DATE_TIME);
        }
        if (type == LocalDate.class) {
            result = new FormFieldDefinition(field.getName(), DATE);
        }
        if (BOOLEAN_CLASSES.contains(type)) {
            result = new FormFieldDefinition(field.getName(), CHECKBOX);
        }
        Annotation fieldAnnotation = getFieldAnnotation(field);
        if(fieldAnnotation != null) {
            if (fieldAnnotation instanceof TextEditor) {
                TextEditor textEditor = (TextEditor) fieldAnnotation;
                result = new FormFieldDefinition(field.getName(), textEditor.html() ? ControlType.HTML_EDITOR : ControlType.TEXT_AREA);
            }
            if (fieldAnnotation instanceof Select) {
                result = new SelectDefinition(field.getName());
            }
            if (fieldAnnotation instanceof FilteredSelect) {
                result = new FilteredSelectDefinition(field.getName());
            }
        }

        return result;
    }
}
