package au.com.ontheboat.api.model.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ChangeOwnerRequestDetailDto {
    private Long id;
    private ChangeOwnerRequestStatus status;
    private String boatName;
    private String submitted;
}
