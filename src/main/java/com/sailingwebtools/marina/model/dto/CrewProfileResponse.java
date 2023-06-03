package com.sailingwebtools.marina.model.dto;

import com.sailingwebtools.marina.model.CrewStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrewProfileResponse {
    private Long id;
    private String uuid;
    
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private CrewStatus status;
    @Builder.Default
    private List<ProfileBoatResponse> ownedBoats = new ArrayList<>();
}
