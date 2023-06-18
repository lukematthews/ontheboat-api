package au.com.ontheboat.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class BoatDetails {
    @Id
    @GeneratedValue
    private Long id;
    @Column
    private String boatName;
    @Column
    private String launchYear;
    @Column
    private String lengthOverall;
    @Column
    private String rig;
    @Column
    private String design;
    @Column
    private String hullMaterial;
    @Column
    private String hullColour;
    @Column(length = 8192)
    private String bio;
}
