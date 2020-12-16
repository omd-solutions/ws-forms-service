package com.omd.ws.forms;

import java.util.List;
import java.util.Set;

public class EntityFormDefinition {

    private String name;
    private List<TabDefinition> tabs;
    private Set<String> filterFields;

    EntityFormDefinition(String name, List<TabDefinition> tabs, Set<String> filterFields) {
        this.name = name;
        this.tabs = tabs;
        this.filterFields = filterFields;
    }

    public String getName() {
        return name;
    }

    public List<TabDefinition> getTabs() {
        return tabs;
    }

    public Set<String> getFilterFields() {
        return filterFields;
    }
}
