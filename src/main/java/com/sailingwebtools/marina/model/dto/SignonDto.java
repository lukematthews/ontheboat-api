package com.sailingwebtools.marina.model.dto;

import com.sailingwebtools.marina.model.Onboard;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignonDto {
    private Onboard onboard;
    private boolean newCrew;

}
