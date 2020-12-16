package com.omd.ws.forms;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

import static com.omd.ws.forms.ControlType.*;
import static com.omd.ws.forms.Conventions.NO_PANEL;
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
    private Class<?> fieldType;
    private String caption;
    private int columns;
    private ControlType controlType;
    private String section;

    public FormFieldDefinition(Field field) throws EntityConfigurationException {
        this(field, null);
    }

    public FormFieldDefinition(Field field, ControlType controlType) throws EntityConfigurationException {
        this.fieldName = field.getName();
        this.fieldType = field.getType();
        if(controlType != null) {
            this.controlType = controlType;
        } else {
            if (fieldType == String.class) {
                this.controlType = TEXT;
            }
            if (NUMBER_CLASSES.contains(fieldType)) {
                this.controlType = NUMBER;
            }
            if (DATE_TIME_CLASSES.contains(fieldType)) {
                this.controlType = DATE_TIME;
            }
            if (fieldType == LocalDate.class) {
                this.controlType = DATE;
            }
            if (BOOLEAN_CLASSES.contains(fieldType)) {
                this.controlType = CHECKBOX;
            }
            if (this.controlType == null) {
                throw new EntityConfigurationException(String.format("Unable to infer a control type for %s", field.getName()));
            }
        }
        FormField formField = field.getAnnotation(FormField.class);
        this.caption = calculateFieldCaption(field, formField);
        this.columns = formField == null ? 12 : formField.columns();
        this.section = formField == null ? NO_PANEL : formField.panel();
    }

    String calculateFieldCaption(Field field, FormField formField) {
        if(formField != null && !formField.caption().isEmpty()) {
            return formField.caption();
        }
        Matcher matcher = compile("([A-Z])").matcher(field.getName());
        String withSpaces = matcher.replaceAll(r -> " " + r.group(1));
        return withSpaces.substring(0, 1).toUpperCase() + withSpaces.substring(1);
    }

    protected boolean isSimpleType() {
        return fieldType == String.class ||
                BOOLEAN_CLASSES.contains(fieldType) ||
                DATE_TIME_CLASSES.contains(fieldType) ||
                NUMBER_CLASSES.contains(fieldType);
    }

    protected Object wrangleValue(String stringValue) {
        if(stringValue == null) {
            return null;
        }
        if(String.class.equals(fieldType)) {
            return stringValue;
        }
        if(Set.of(Boolean.class, boolean.class).contains(fieldType)) {
            return Boolean.valueOf(stringValue);
        }
        if(Set.of(Integer.class, int.class).contains(fieldType)) {
            return Integer.valueOf(stringValue);
        }
        if(Set.of(Double.class, double.class).contains(fieldType)) {
            return Double.valueOf(stringValue);
        }
        if(Set.of(Float.class, float.class).contains(fieldType)) {
            return Float.valueOf(stringValue);
        }
        if(Set.of(Short.class, short.class).contains(fieldType)) {
            return Short.valueOf(stringValue);
        }
        if(Set.of(Boolean.class, boolean.class).contains(fieldType)) {
            return Boolean.valueOf(stringValue);
        }
        if(Date.class.equals(fieldType)) {
            try {
                return DateFormat.getInstance().parse(stringValue);
            } catch (ParseException e) {
                throw new RuntimeException(String.format("Unexpected parse exception while wrangling date string %s", stringValue), e);
            }
        }
        if(LocalDate.class.equals(fieldType)) {
            return LocalDate.parse(stringValue);
        }
        if(LocalDateTime.class.equals(fieldType)) {
            return LocalDateTime.parse(stringValue);
        }
        if(ZonedDateTime.class.equals(fieldType)) {
            return ZonedDateTime.parse(stringValue);
        }
        throw new RuntimeException(String.format("Unable to wrangle string values to type %s", fieldType.getName()));
    }

    protected Class<?> getFieldType() {
        return fieldType;
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
