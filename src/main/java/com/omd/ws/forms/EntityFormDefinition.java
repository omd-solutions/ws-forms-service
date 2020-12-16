package com.omd.ws.forms;

import java.util.List;
import java.util.Set;

public class EntityFormDefinition {

    private String name;
    private List<PanelDefinition> panels;
    private Set<String> filterFields;

    EntityFormDefinition(String name, List<PanelDefinition> panels, Set<String> filterFields) {
        this.name = name;
        this.panels = panels;
        this.filterFields = filterFields;
    }

    public String getName() {
        return name;
    }

    public List<PanelDefinition> getPanels() {
        return panels;
    }

    public Set<String> getFilterFields() {
        return filterFields;
    }
}
