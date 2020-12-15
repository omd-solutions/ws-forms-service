package com.omd.ws.forms;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SimpleFormsService implements FormsService {

    private Map<String, EntityFormConfiguration> configurations = new HashMap<>();

    @Override
    public void registerEntityForm(Class<?> entityClass) throws EntityConfigurationException {
        EntityFormConfiguration configuration = new EntityFormConfiguration(entityClass);
        configurations.put(configuration.getEntityName(), configuration);
    }

    @Override
    public EntityFormDefinition getEntityFormDefinition(String entityName) {
        return getEntityFormConfiguration(entityName).getDefinition();
    }

    @Override
    public List<?> getFieldOptions(String entity, String fieldName, Map<String, String> fieldValues) {
        EntityFormConfiguration config = getEntityFormConfiguration(entity);
        FormFieldDefinition fieldDef = config.getFieldDefinition(fieldName);
        if(fieldDef instanceof FilteredSelectDefinition) {
            FilteredSelectDefinition filteredSelectDef = (FilteredSelectDefinition) fieldDef;
            Object resolvedFilterValue = resolveFieldValue(config, filteredSelectDef.getFilteredBy(), fieldValues);
            return filteredSelectDef.getValues(resolvedFilterValue);
        } else {
            throw new RuntimeException(String.format("The field %s is not defined as a filtered select control, values should have been provided in the original form config", fieldName));
        }
    }

    Object resolveFieldValue(EntityFormConfiguration config, String fieldName, Map<String, String> fieldValues) {
        FormFieldDefinition fieldDef = config.getFieldDefinition(fieldName);
        if(fieldDef instanceof SelectDefinition) {
            SelectDefinition selectDef = (SelectDefinition) fieldDef;
            return selectDef.getValue(fieldValues.get(fieldName));
        }
        if(fieldDef instanceof FilteredSelectDefinition) {
            FilteredSelectDefinition filteredSelectDef = (FilteredSelectDefinition) fieldDef;
            Object filter = resolveFieldValue(config, filteredSelectDef.getFilteredBy(), fieldValues);
            return filteredSelectDef.getValue(fieldValues.get(fieldName), filter);
        }
        return fieldDef.wrangleValue(fieldValues.get(fieldName));
    }

    EntityFormConfiguration getEntityFormConfiguration(String entityName)  {
        EntityFormConfiguration configuration = configurations.get(entityName);
        if(configuration == null) {
            throw new RuntimeException(String.format("%s is not a registered entity", entityName));
        }
        return configuration;
    }


}
