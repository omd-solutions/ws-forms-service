package com.omd.ws.forms;

import java.util.List;
import java.util.Map;

public interface FormsService {

    void registerEntityForm(Class<?> entityClass) throws EntityConfigurationException;

    EntityFormDefinition getEntityFormDefinition(String entityName);

    List<?> getFieldOptions(String entity, String fieldName, Map<String, String> fieldValues);
}
