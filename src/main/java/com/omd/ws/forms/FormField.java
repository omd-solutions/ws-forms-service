package com.omd.ws.forms;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.omd.ws.forms.Conventions.NO_PANEL;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FormField {

    String caption() default "";
    int columns() default 12;
    String panel() default NO_PANEL;
}
