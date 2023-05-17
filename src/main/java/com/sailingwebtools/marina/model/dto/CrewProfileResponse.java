package com.sailingwebtools.marina.model.dto;

import com.sailingwebtools.marina.model.Boat;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class CrewProfileResponse {
    private String username;
    private String firstName;
    private String lastName;
    @Builder.Default
    private List<Boat> ownedBoats = new ArrayList<>();
}
