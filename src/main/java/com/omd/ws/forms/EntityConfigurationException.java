package com.omd.ws.forms;

public class EntityConfigurationException extends Exception {

    public EntityConfigurationException(String msg) {
        super(msg);
    }

    public EntityConfigurationException(String msg, Throwable t) {
        super(msg, t);
    }
}
