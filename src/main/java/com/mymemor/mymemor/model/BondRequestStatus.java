package com.mymemor.mymemor.model;

public enum BondRequestStatus {
    ACCEPT(1),
    PENDING(2),
    DECLINE(3);

    private int value;

    BondRequestStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static BondRequestStatus fromValue(int value) {
        switch (value) {
            case 1:
                return BondRequestStatus.ACCEPT;
            case 2:
                return BondRequestStatus.PENDING;
            case 3:
                return BondRequestStatus.DECLINE;
        }
        return BondRequestStatus.PENDING;
    }
}