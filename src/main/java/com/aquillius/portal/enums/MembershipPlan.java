package com.aquillius.portal.enums;


public enum MembershipPlan {

    ANNUAL(2),
    MONTHLY(1);

    final int id;

    MembershipPlan(int id){
        this.id=id;
    }

    public int getId() {
        return id;
    }

}
