package com.sailingwebtools.marina.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CrewOnboardRequest {
    private Long boatId;
    private String firstName;
    private String lastName;
    private String mobile;
    private String email;
    private boolean rememberMe;
}
