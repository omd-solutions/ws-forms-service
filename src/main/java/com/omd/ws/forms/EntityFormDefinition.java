package com.omd.ws.forms;

import java.util.List;
import java.util.Set;

public class EntityFormDefinition {

    private String name;
    private SectionType sectionType;
    private List<SectionDefinition> sections;
    private Set<String> filterFields;

    EntityFormDefinition(String name, SectionType sectionType, List<SectionDefinition> sections, Set<String> filterFields) {
        this.name = name;
        this.sectionType = sectionType;
        this.sections = sections;
        this.filterFields = filterFields;
    }

    public String getName() {
        return name;
    }

    public SectionType getSectionType() {
        return sectionType;
    }

    public List<SectionDefinition> getSections() {
        return sections;
    }

    public Set<String> getFilterFields() {
        return filterFields;
    }
}
