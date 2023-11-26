package com.aquillius.portal.enums;

public enum PaymentMethod {
    CREDIT_CARD("Credit Card"),
    ACH("ACH (Bank)");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

