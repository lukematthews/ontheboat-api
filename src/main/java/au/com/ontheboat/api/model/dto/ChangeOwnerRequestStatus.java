package au.com.ontheboat.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
public enum ChangeOwnerRequestStatus {
    SUBMITTED("submitted"),
    IN_PROGRESS("inProgress"),
    APPROVED("approved"),
    REJECTED("rejected"),
    CANCELLED("cancelled");

    @Getter
    private String status;
}
