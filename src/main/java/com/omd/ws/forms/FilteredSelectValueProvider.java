package com.omd.ws.forms;

import java.util.List;

@FunctionalInterface
public interface FilteredSelectValueProvider<T, F> {

    List<T> getValues(F filter);
}
