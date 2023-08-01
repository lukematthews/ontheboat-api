package au.com.ontheboat.api.model.dto;

import au.com.ontheboat.api.model.BoatDetails;
import au.com.ontheboat.api.model.BoatMedia;
import au.com.ontheboat.api.model.Handicap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoatDto {
    private Long id;
    private String boatName;
    private String sailNumber;
    private String contact;
    private String design;
    private Boolean archived;
    private BoatDetails boatDetails;
    private List<Handicap> handicaps;
    private Set<CrewProfileResponse> owners;
    private Set<BoatMedia> boatMedia;
}
