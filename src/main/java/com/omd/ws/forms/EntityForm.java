package com.omd.ws.forms;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityForm {

    String name() default "";

    SectionType sectionType() default SectionType.PANELS;

    Section [] sections() default {};
}
