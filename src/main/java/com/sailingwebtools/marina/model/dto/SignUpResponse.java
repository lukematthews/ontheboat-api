package com.sailingwebtools.marina.model.dto;

import com.sailingwebtools.marina.model.Crew;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignUpResponse {
    private Crew crew;
}
