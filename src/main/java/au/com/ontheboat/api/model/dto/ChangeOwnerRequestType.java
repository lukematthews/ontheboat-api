package au.com.ontheboat.api.model.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ChangeOwnerRequestType {
    SOLE_OWNER("sole"),
    PARTNER("partner"),
    OTHER_PARTY("other"),
    REMOVE("remove");
    @Getter
    @JsonValue
    private String type;
}
