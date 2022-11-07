package com.tomekw.poszkole.payments;

public enum PaymentStatus {

    DONE("done"),
    WAITING("waiting"),
    CANCELED("canceled"),
    EXPERIMENT("fff");

    String description;

    PaymentStatus(String description) {
        this.description = description;
    }
}
