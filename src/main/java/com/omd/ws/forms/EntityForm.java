package com.omd.ws.forms;

public @interface EntityForm {

    String name() default "";

    SectionType sectionType() default SectionType.PANELS;

    Section [] sections() default {};
}
