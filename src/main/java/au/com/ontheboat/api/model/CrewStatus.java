package au.com.ontheboat.api.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
public enum CrewStatus {

    PLACEHOLDER("Placeholder"),
    ACTIVE("Active"),
    INACTIVE("Inactive");

    @JsonValue
    private String status;
}
