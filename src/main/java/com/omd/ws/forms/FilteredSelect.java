package com.omd.ws.forms;

public @interface FilteredSelect {

    String displayField() default "";
    Class<? extends FilteredSelectValueProvider> valueProvider();
    String filteredBy();
}
