package com.sailingwebtools.marina.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Data
public class OnboardResponse {
    private Long boatId;
    private LocalDate day;
    private UUID uuid;
    private String name;
}
