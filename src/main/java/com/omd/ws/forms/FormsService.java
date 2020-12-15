package com.omd.ws.forms;

import java.util.List;
import java.util.Map;

public interface FormsService {

    void registerEntityForm(Class<?> entityClass) throws EntityConfigurationException;

    EntityFormDefinition getEntityFormDefinition(String entityName);

    Map<String, List<?>> getFieldOptions(String entity, Map<String, String> fieldValues);
}
