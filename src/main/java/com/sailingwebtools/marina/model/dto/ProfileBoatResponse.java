package com.sailingwebtools.marina.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileBoatResponse {
    private Long id;
    private String boatName;
}
