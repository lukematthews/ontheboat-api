package au.com.ontheboat.api.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AuditEntryDto {
    private LocalDateTime eventTime;
    private String description;
    private Long relatedItem;
    private String relatedItemType;
}
