package com.sailingwebtools.marina.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangeOwnerRequest {
    private Long boatId;
    private Long crewId;
    private ChangeOwnerRequestType requestType;
}
