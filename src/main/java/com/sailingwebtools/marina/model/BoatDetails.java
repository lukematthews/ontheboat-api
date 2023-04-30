package com.sailingwebtools.marina.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Objects;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class BoatDetails {
    @Id
    @GeneratedValue
    private Long id;
    @OneToOne
    @ToString.Exclude
    private Boat boat;
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

    @ToString.Include
    public String getBoat() {
        return Objects.isNull(boat) ? null : boat.getId() + " " + boat.getBoatName();
    }
}
