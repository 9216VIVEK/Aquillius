package com.aquillius.portal.enums;

public enum AnnualPaymentType {

    ANNUAL(2),
    MONTHLY(1);

    final int id;

    AnnualPaymentType(int id){
        this.id=id;
    }

    public int getId() {
        return id;
    }
}
