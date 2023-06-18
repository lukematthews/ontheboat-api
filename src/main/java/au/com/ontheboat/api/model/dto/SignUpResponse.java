package au.com.ontheboat.api.model.dto;

import au.com.ontheboat.api.model.Crew;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignUpResponse {
    private Crew crew;
}
