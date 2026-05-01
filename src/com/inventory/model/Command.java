package com.inventory.model;
import java.io.Serializable;

public class Command implements Serializable {
    private static final long serialVersionUID = 1L;
    public enum CommandType {
        GET_INVENTORY,
        ADD_ITEM,
        UPDATE_ITEM,
        DELETE_ITEM,
        SEARCH,
        LOGIN,
        GET_LOW_STOCK,
        GET_SUPPLIERS,
        ADD_SUPPLIER,
        CREATE_PURCHASE_ORDER,
        GET_PURCHASE_ORDERS,
        UPDATE_ORDER_STATUS,
        REGISTER
    }
    private CommandType type;
    @SuppressWarnings("serial")
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
