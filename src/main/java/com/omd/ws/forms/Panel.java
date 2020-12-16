package com.omd.ws.forms;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.omd.ws.forms.Conventions.NO_TAB;

@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Panel {

    String name();
    String caption();
    String tab() default NO_TAB;

}
