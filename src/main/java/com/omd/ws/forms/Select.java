package com.omd.ws.forms;

public @interface Select {

    String displayField() default "";
    Class<? extends SelectValueProvider> valueProvider();

}
