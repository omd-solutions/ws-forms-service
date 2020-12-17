package com.omd.ws.forms;

import java.lang.reflect.Field;

import static com.omd.ws.forms.ControlType.TEXT;

public class TextDefinition extends FormFieldDefinition {

    private String validationRegex;
    private String validationMessage;

    public TextDefinition(Field field, Text annotation) throws EntityConfigurationException {
        super(field, TEXT);
        if(!annotation.validationRegex().isEmpty()) {
            this.validationRegex = annotation.validationRegex();
            this.validationMessage = annotation.validationMessage();
        }
    }

    public String getValidationRegex() {
        return validationRegex;
    }

    public String getValidationMessage() {
        return validationMessage;
    }
}
