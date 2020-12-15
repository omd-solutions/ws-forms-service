package com.omd.ws.forms;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;

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
        return getEntityFormConfiguration(entityName).getFormDefinition();
    }

    @Override
    public Map<String, List<?>> getFieldOptions(String entity, Map<String, String> fieldValues) {
        EntityFormConfiguration config = getEntityFormConfiguration(entity);
        List<FilteredSelectDefinition> filteredSelectDefs = config.getFieldDefinitions(FilteredSelectDefinition.class);
        Map<String, List<?>> results = new HashMap<>(filteredSelectDefs.size());
        for(FilteredSelectDefinition filteredSelectDef : filteredSelectDefs) {
            Object resolvedFilterValue = resolveFieldValue(config, filteredSelectDef.getFilteredBy(), fieldValues);
            List<?> options = resolvedFilterValue != null ? filteredSelectDef.getValues(resolvedFilterValue) : emptyList();
            results.put(filteredSelectDef.getFieldName(), options);
        }
        return results;
    }

    Object resolveFieldValue(EntityFormConfiguration config, String fieldName, Map<String, String> fieldValues) {
        if(fieldValues.get(fieldName) == null) {
            return null;
        }
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
