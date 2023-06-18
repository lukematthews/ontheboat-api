package au.com.ontheboat.api.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangeOwnerRequestDto {
    private Long boatId;
    private Long crewId;
    private ChangeOwnerRequestType requestType;
}
