package com.mourathi.entity;

import java.util.Set;

public enum Role {

    ROLE_ADMIN(
            Permission.values()
    ),


    ROLE_CUSTOMER(
            Permission.USER_VIEW_OWN,
            Permission.PRODUCT_VIEW,
            Permission.CART_VIEW_OWN,
            Permission.CART_CREATE,
            Permission.CART_UPDATE,
            Permission.ORDER_CREATE,
            Permission.ORDER_VIEW_OWN
    ),


    ROLE_PRODUCT_MANAGER(
            Permission.PRODUCT_VIEW,
            Permission.PRODUCT_CREATE,
            Permission.PRODUCT_UPDATE,
            Permission.PRODUCT_DELETE
    ),


    ROLE_INVENTORY_MANAGER(
            Permission.INVENTORY_VIEW,
            Permission.INVENTORY_UPDATE
    ),


    ROLE_ORDER_MANAGER(
            Permission.ORDER_VIEW_ALL,
            Permission.ORDER_UPDATE_STATUS,
            Permission.ORDER_CANCEL
    ),


    ROLE_SUPPORT_AGENT(
            Permission.USER_VIEW_ALL,
            Permission.ORDER_VIEW_ALL,
            Permission.ORDER_UPDATE_STATUS
    ),


    ROLE_FINANCE_MANAGER(
            Permission.PAYMENT_VIEW,
            Permission.REFUND_PROCESS
    );


    private final Set<Permission> permissions;


    Role(Permission... permissions) {
        this.permissions = Set.of(permissions);
    }


    public Set<Permission> getPermissions() {
        return permissions;
    }
}