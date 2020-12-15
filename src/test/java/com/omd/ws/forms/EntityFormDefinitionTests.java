package com.omd.ws.forms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EntityFormDefinitionTests {

    private SimpleFormsService formsService;
    private ObjectMapper mapper;

    EntityFormDefinitionTests() {
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @BeforeEach
    void setupEach() {
        formsService = new SimpleFormsService();
    }

    @Test
    void testEmployeeEntity() throws Exception  {
        String expectedJson = IOUtils.resourceToString("/EmployeeEntity.json", StandardCharsets.UTF_8);
        EntityFormConfiguration actual = new EntityFormConfiguration(EmployeeEntity.class);
        assertEquals(expectedJson, mapper.writeValueAsString(actual.getFormDefinition()));
    }
}
