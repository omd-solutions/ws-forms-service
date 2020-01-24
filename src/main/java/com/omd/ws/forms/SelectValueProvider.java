package com.omd.ws.forms;

import java.util.List;

@FunctionalInterface
public interface SelectValueProvider<T> {

    List<T> getValues();
}
