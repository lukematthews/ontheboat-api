package au.com.ontheboat.api.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
public class ChangeOwnerRequestDetailDto {
    private Long id;
    private ChangeOwnerRequestStatus status;
    private String boatName;
    private String submitted;

    private String crewName;
    private String submittedBy;

    private ChangeOwnerRequestType requestType;
    @Builder.Default
    private List<AuditEntryDto> auditEntries = new ArrayList<>();
}
