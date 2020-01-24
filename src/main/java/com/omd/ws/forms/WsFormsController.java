package com.omd.ws.forms;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WsFormsController {

    private FormsService formsService;

    WsFormsController(FormsService formsService) {
        this.formsService = formsService;
    }

    @RequestMapping(value = "/api/forms", method = RequestMethod.GET)
    public EntityFormDefinition getEntityForm(@RequestParam String entity) {
        return formsService.getEntityFormDefinition(entity);
    }
}
