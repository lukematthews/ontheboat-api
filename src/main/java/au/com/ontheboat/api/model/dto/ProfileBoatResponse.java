package au.com.ontheboat.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileBoatResponse {
    private Long id;
    private String boatName;
    private String externalId;
}
