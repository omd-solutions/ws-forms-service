package com.omd.ws.forms;

public interface FormsService {

    void registerEntityForm(Class<?> entityClass) throws EntityConfigurationException;

    EntityFormDefinition getEntityFormDefinition(String entityName);
}
