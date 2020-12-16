package com.omd.ws.forms;

import java.util.ArrayList;
import java.util.List;

public class TabDefinition {

    private int orderIndex;
    private String caption;
    private List<PanelDefinition> panels = new ArrayList<>();

    TabDefinition(int orderIndex, String caption) {
        this.orderIndex = orderIndex;
        this.caption = caption;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public String getCaption() {
        return caption;
    }

    public List<PanelDefinition> getPanels() {
        return panels;
    }

    void addPanel(PanelDefinition panel) {
        panels.add(panel);
    }
}
