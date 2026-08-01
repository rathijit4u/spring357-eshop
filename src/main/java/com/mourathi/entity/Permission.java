package com.mourathi.entity;

public enum Permission {

    // User
    USER_VIEW_OWN,
    USER_VIEW_ALL,
    USER_CREATE,
    USER_UPDATE,
    USER_DELETE,
    USER_ROLE_ASSIGNEE,

    // Product
    PRODUCT_VIEW,
    PRODUCT_CREATE,
    PRODUCT_UPDATE,
    PRODUCT_DELETE,

    // Inventory
    INVENTORY_VIEW,
    INVENTORY_UPDATE,

    // Cart
    CART_VIEW_OWN,
    CART_VIEW_ALL,
    CART_CREATE,
    CART_UPDATE,
    CART_DELETE,

    // Order
    ORDER_CREATE,
    ORDER_VIEW_OWN,
    ORDER_VIEW_ALL,
    ORDER_UPDATE_STATUS,
    ORDER_CANCEL,

    // Finance
    PAYMENT_VIEW,
    REFUND_PROCESS
}
