package com.omd.ws.forms;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

import static com.omd.ws.forms.ControlType.*;
import static com.omd.ws.forms.Conventions.NO_SECTION;
import static java.util.regex.Pattern.compile;

public class FormFieldDefinition {

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

    private String fieldName;
    private String caption;
    private int columns;
    private ControlType controlType;
    private String section;

    public FormFieldDefinition(Field field) throws EntityConfigurationException {
        this(field, null);
    }

    public FormFieldDefinition(Field field, ControlType controlType) throws EntityConfigurationException {
        this.fieldName = field.getName();
        if(controlType != null) {
            this.controlType = controlType;
        } else {
            Class<?> type = field.getType();
            if (type == String.class) {
                this.controlType = TEXT;
            }
            if (NUMBER_CLASSES.contains(type)) {
                this.controlType = NUMBER;
            }
            if (DATE_TIME_CLASSES.contains(type)) {
                this.controlType = DATE_TIME;
            }
            if (type == LocalDate.class) {
                this.controlType = DATE;
            }
            if (BOOLEAN_CLASSES.contains(type)) {
                this.controlType = CHECKBOX;
            }
            if (this.controlType == null) {
                throw new EntityConfigurationException(String.format("Unable to infer a control type for %s", field.getName()));
            }
        }
        FormField formField = field.getAnnotation(FormField.class);
        this.caption = calculateFieldCaption(field, formField);
        this.columns = formField == null ? 12 : formField.columns();
        this.section = formField == null ? NO_SECTION : formField.section();
    }

    String calculateFieldCaption(Field field, FormField formField) {
        if(formField != null && !formField.caption().isEmpty()) {
            return formField.caption();
        }
        Matcher matcher = compile("([A-Z])").matcher(field.getName());
        String withSpaces = matcher.replaceAll(r -> " " + r.group(1));
        return withSpaces.substring(0, 1).toUpperCase() + withSpaces.substring(1);
    }

    protected boolean isSimpleType(Field field) {
        Class<?> type = field.getType();
        return type == String.class ||
                BOOLEAN_CLASSES.contains(type) ||
                DATE_TIME_CLASSES.contains(type) ||
                NUMBER_CLASSES.contains(type);
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getCaption() {
        return caption;
    }

    public int getColumns() {
        return columns;
    }

    public ControlType getControlType() {
        return controlType;
    }

    String getSection() {
        return section;
    }
}
