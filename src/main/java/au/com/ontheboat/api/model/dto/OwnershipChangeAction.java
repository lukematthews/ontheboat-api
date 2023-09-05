package au.com.ontheboat.api.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OwnershipChangeAction {
    private ActionType action;
    private Long id;
}
