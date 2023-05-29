package com.sailingwebtools.marina.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
public enum CrewStatus {

    PLACEHOLDER("placeholder"),
    ACTIVE("active"),
    INACTIVE("inactive");

    @Getter
    private String status;
}
