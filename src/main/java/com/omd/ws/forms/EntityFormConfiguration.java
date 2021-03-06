package com.omd.ws.forms;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static com.omd.ws.forms.Conventions.NO_PANEL;
import static com.omd.ws.forms.Conventions.NO_TAB;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toList;

class EntityFormConfiguration {

    private String entityName;
    private final EntityFormDefinition formDefinition;
    private final Map<String, FormFieldDefinition> fieldDefinitions = new HashMap<>();

    private static final Set<Class<?>> FIELD_ANNOTATION_CLASSES = new HashSet<>(Arrays.asList(
            Text.class,
            TextEditor.class,
            Select.class,
            FilteredSelect.class
    ));

    EntityFormConfiguration(Class<?> entityClass) throws EntityConfigurationException {
        entityName = entityClass.getName();
        Map<String, TabDefinition> tabs = new HashMap<>();
        Map<String, PanelDefinition> panels = new HashMap<>();
        EntityForm entityForm = entityClass.getDeclaredAnnotation(EntityForm.class);
        if (entityForm != null) {
            if (!entityForm.name().isEmpty()) {
                entityName = entityForm.name();
            }
            boolean tabsUsed = false;
            for (int i = 0; i < entityForm.panels().length; i++) {
                PanelDefinition panelDef = new PanelDefinition(i, entityForm.panels()[i].caption());
                panels.put(entityForm.panels()[i].name(), panelDef);
                String tabName = entityForm.panels()[i].tab();
                if(tabName.equals(NO_TAB) && tabsUsed) {
                    throw new EntityConfigurationException(String.format("The panel '%s' specifies a tab but there are already panels without a tab. Where tabs are used all panels must specify a tab", entityForm.panels()[i].name()));
                } else if (!tabName.equals(NO_TAB)) {
                    tabsUsed = true;
                }
                TabDefinition tabDef = tabs.get(tabName);
                tabDef = tabDef == null ? new TabDefinition(i, tabName) : tabDef;
                tabDef.addPanel(panelDef);
                tabs.put(tabName, tabDef);
            }
        }
        if (panels.isEmpty()) {
            PanelDefinition noPanel = new PanelDefinition(0, NO_PANEL);
            panels.put(NO_PANEL, noPanel);
            TabDefinition noTab = new TabDefinition(0, NO_TAB);
            noTab.addPanel(noPanel);
            tabs.put(NO_TAB, noTab);
        }
        Field[] fields = entityClass.getDeclaredFields();
        Set<String> filterFields = new HashSet<>();
        for (Field field : fields) {
            FormsIgnore ignoreAnnotation = field.getAnnotation(FormsIgnore.class);
            if (ignoreAnnotation == null) {
                FormFieldDefinition fieldDefinition = createFormFieldDefinition(field);
                addFormField(fieldDefinition, panels, tabs);
                fieldDefinitions.put(fieldDefinition.getFieldName(), fieldDefinition);
                if(fieldDefinition instanceof FilteredSelectDefinition) {
                    String filteredBy = ((FilteredSelectDefinition) fieldDefinition).getFilteredBy();
                    filterFields.add(filteredBy);
                }
            }
        }

        formDefinition = new EntityFormDefinition(entityName, tabs.values().stream()
                .sorted(comparingInt(TabDefinition::getOrderIndex))
                .collect(toList()), filterFields);
    }

    void addFormField(FormFieldDefinition fieldDefinition, Map<String, PanelDefinition> panels, Map<String, TabDefinition> tabs) throws EntityConfigurationException {
        String panel = fieldDefinition.getPanel();
        String tab = fieldDefinition.getTab();
        PanelDefinition panelDefinition = panels.get(panel);
        if (panelDefinition == null && tab.equals(NO_TAB)) {
            if (panel.equals(NO_PANEL)) {
                throw new EntityConfigurationException(String.format("Unable to place field '%s', you must specify a panel or a tab for each field if the entity defines panels", fieldDefinition.getFieldName()));
            } else {
                throw new EntityConfigurationException(String.format("Unable to place field '%s', the specified panel %s does not exist on this entity", fieldDefinition.getFieldName(), panel));
            }
        }
        if(panelDefinition != null) {
            //We don't check tab here - if you specify both then tab gets ignored
            panelDefinition.addField(fieldDefinition);
        } else {
            //Field is in a tab with no panel
            if(tabs.containsKey(NO_TAB)) {
                throw new EntityConfigurationException(String.format("The field '%s' specifies a tab but there are already panels without a tab. Where tabs are used all panels must specify a tab", fieldDefinition.getFieldName()));
            }
            TabDefinition tabDef = tabs.get(tab);
            tabDef = tabDef == null ? new TabDefinition(Integer.MAX_VALUE, tab) : tabDef;
            PanelDefinition tabPanel = tabDef.getPanels().stream().filter(p -> NO_PANEL.equals(p.getCaption())).findAny().orElse(null);
            if(tabPanel == null) {
                tabPanel = new PanelDefinition(0, NO_PANEL);
                tabDef.addPanel(tabPanel);
            }
            if(tabDef.getPanels().size() > 1) {
                throw new EntityConfigurationException(String.format("The tab '%s' has a mix of panels and fields without panels. A tab may only have one or the other", tab));
            }
            tabPanel.addField(fieldDefinition);
            tabs.put(tab, tabDef);
        }
    }

    FormFieldDefinition createFormFieldDefinition(Field field) throws EntityConfigurationException {
        Annotation fieldAnnotation = getFieldAnnotation(field);
        if (fieldAnnotation != null) {
            if (fieldAnnotation instanceof Text) {
                return new TextDefinition(field, (Text) fieldAnnotation);
            }
            if (fieldAnnotation instanceof TextEditor) {
                TextEditor textEditor = (TextEditor) fieldAnnotation;
                return new FormFieldDefinition(field, textEditor.html() ? ControlType.HTML_EDITOR : ControlType.TEXT_AREA);
            }
            if (fieldAnnotation instanceof Select) {
                return new SelectDefinition(field, (Select) fieldAnnotation);
            }
            if (fieldAnnotation instanceof FilteredSelect) {
                return new FilteredSelectDefinition(field, (FilteredSelect) fieldAnnotation);
            }
        }
        return new FormFieldDefinition(field);
    }

    Annotation getFieldAnnotation(Field field) {
        List<Annotation> annotations = Arrays.stream(field.getAnnotations())
                .filter(a -> FIELD_ANNOTATION_CLASSES.contains(a.annotationType()))
                .collect(toList());
        if (annotations.size() > 1) {
            throw new RuntimeException(String.format("The field '%s' should only have one of the following %s", field.getName(),
                    annotations.stream().map(a -> a.annotationType().getSimpleName()).collect(Collectors.joining(", "))));
        }
        return annotations.isEmpty() ? null : annotations.get(0);
    }

    String getEntityName() {
        return entityName;
    }

    EntityFormDefinition getFormDefinition() {
        return formDefinition;
    }

    <T extends FormFieldDefinition> List<T> getFieldDefinitions(Class<T> formFieldClass) {
        return fieldDefinitions.values().stream()
                .filter(f -> f.getClass() == formFieldClass)
                .map(f -> ((T) f))
                .collect(toList());
    }

    FormFieldDefinition getFieldDefinition(String fieldName) {
        return fieldDefinitions.get(fieldName);
    }
}
