package com.omd.ws.forms;

import java.lang.reflect.Field;

import static com.omd.ws.forms.ControlType.TEXT;

public class TextDefinition extends FormFieldDefinition {

    private String validationRegex;
    private String validationMessage;
    private boolean masked;

    public TextDefinition(Field field, Text annotation) throws EntityConfigurationException {
        super(field, TEXT);
        if(!annotation.validationRegex().isEmpty()) {
            this.validationRegex = annotation.validationRegex();
            this.validationMessage = annotation.validationMessage();
        }
        this.masked = annotation.masked();
    }

    public String getValidationRegex() {
        return validationRegex;
    }

    public String getValidationMessage() {
        return validationMessage;
    }

    public boolean isMasked() {
        return masked;
    }
}
