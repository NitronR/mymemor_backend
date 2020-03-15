package com.mymemor.mymemor.model;

import com.mymemor.mymemor.exceptions.InvalidBondActionException;

public enum BondAction {
    ACCEPT(0), DECLINE(1);

    int value;

    BondAction(int value) {
        this.value = value;
    }

    public static BondAction fromValue(int value) throws InvalidBondActionException {
        switch (value) {
            case 0:
                return ACCEPT;
            case 1:
                return DECLINE;
            default:
                throw new InvalidBondActionException("No such action exist");
        }
    }
}
