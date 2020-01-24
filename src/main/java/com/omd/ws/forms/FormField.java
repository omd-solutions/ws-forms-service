package com.omd.ws.forms;

import static com.omd.ws.forms.Conventions.NO_SECTION;

public @interface FormField {

    String caption() default "";
    int columns() default 12;
    String section() default NO_SECTION;
}
