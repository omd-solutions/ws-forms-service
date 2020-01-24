package com.omd.ws.forms;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public enum ControlType {

    TEXT,
    TEXT_AREA,
    HTML_EDITOR,
    SELECT,
    FILTERED_SELECT,
    DATE,
    TIME,
    DATE_TIME,
    NUMBER,
    SLIDER,
    INCREMENTER,
    PROGRESS,
    CHECKBOX,
    RADIO;

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

    static ControlType getControlType(Field field) {
        Class type = field.getType();
        if (type == String.class) {
            return TEXT;
        }
        if (NUMBER_CLASSES.contains(type)) {
            return NUMBER;
        }
        if (DATE_TIME_CLASSES.contains(type)) {
            return DATE_TIME;
        }
        if (type == LocalDate.class) {
            return DATE;
        }
        if (BOOLEAN_CLASSES.contains(type)) {
            return CHECKBOX;
        }
        return null;
    }

    static ControlType getControlType(Annotation annotation) {
        if (annotation instanceof TextEditor) {
            TextEditor textEditor = (TextEditor) annotation;
            return textEditor.html() ? ControlType.HTML_EDITOR : ControlType.TEXT_AREA;
        }
        if (annotation instanceof Select) {
            return ControlType.SELECT;
        }
        if (annotation instanceof FilteredSelect) {
            return ControlType.FILTERED_SELECT;
        }
        return null;
    }


}
