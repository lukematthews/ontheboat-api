package au.com.ontheboat.api.model.dto;

import au.com.ontheboat.api.model.Onboard;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignonDto {
    private Onboard onboard;
    private boolean newCrew;

}
