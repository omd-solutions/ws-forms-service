package com.omd.ws.forms;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class WsFormsController {

    private FormsService formsService;

    WsFormsController(FormsService formsService) {
        this.formsService = formsService;
    }

    @RequestMapping(value = "/api/forms/{entity}", method = RequestMethod.GET)
    public EntityFormDefinition getEntityForm(@PathVariable String entity) {
        return formsService.getEntityFormDefinition(entity);
    }

    @RequestMapping(value = "/api/forms/{entity}/{fieldName}", method = RequestMethod.POST)
    public List<?> getFieldOptions(@PathVariable String entity, @PathVariable String fieldName,
                                                @RequestBody Map<String, String> fieldValues) {
        return formsService.getFieldOptions(entity, fieldName, fieldValues);
    }
}
