package com.sailingwebtools.marina.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class CrewProfileResponse {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    @Builder.Default
    private List<ProfileBoatResponse> ownedBoats = new ArrayList<>();
}
