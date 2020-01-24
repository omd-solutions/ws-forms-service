package com.omd.ws.forms;

import java.util.List;

public class EntityFormDefinition {

    private String name;
    private SectionType sectionType;
    private List<SectionDefinition> sections;

    EntityFormDefinition(String name, SectionType sectionType, List<SectionDefinition> sections) {
        this.name = name;
        this.sectionType = sectionType;
        this.sections = sections;
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
}
