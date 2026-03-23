package com.inventory.model;

import java.io.Serializable;

public class Command implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum CommandType {
        GET_INVENTORY, ADD_ITEM, UPDATE_ITEM, DELETE_ITEM, SEARCH
    }

    private CommandType type;
    private Object payload;

    public Command(CommandType type, Object payload) {
        this.type = type;
        this.payload = payload;
    }

    public CommandType getType() {
        return type;
    }

    public Object getPayload() {
        return payload;
    }
}
