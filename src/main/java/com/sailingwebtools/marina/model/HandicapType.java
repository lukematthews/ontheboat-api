package com.sailingwebtools.marina.model;

import lombok.Getter;

public enum HandicapType {
    AMS("AMS Rating:"),
    ORC("ORC ~ AP / WL:"),
    IRC("IRC Rating:"),
    ORC_INTL("ORC INTL ~ AP / WL:"),
    ORC_NSCL("ORC NSCL ~ AP / WL:"),
    ORC_CLUB("ORC CLUB ~ AP / WL:");
    @Getter
    private final String label;

    HandicapType(String label) {
        this.label = label;
    }
}
