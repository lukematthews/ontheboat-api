package au.com.ontheboat.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoatUpdateDto {
    private Long id;
    private String boatName;
    private String sailNumber;
    private String bio;
    private String design;
    private String hullColour;
    private String hullMaterial;
    private String launchYear;
    private String lengthOverall;
    private String rig;
}
