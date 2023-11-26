package com.aquillius.portal.enums;

public enum AddOnPlan {

    ANNUAL(2),
    MONTHLY(1);

    final int id;

    AddOnPlan(int id){
        this.id=id;
    }

    public int getId() {
        return id;
    }

}
