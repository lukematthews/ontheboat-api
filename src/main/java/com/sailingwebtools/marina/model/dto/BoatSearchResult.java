package com.sailingwebtools.marina.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BoatSearchResult {
    private Long id;
    private String boatName;
    private String sailNumber;
}
