package com.sailingwebtools.marina.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Handicap {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Boat boat;
    @Column
    @JsonIgnore
    private HandicapType handicapType;
    @Column
    private String rating;
    @Column
    private String certificate;
    @Column
    private LocalDate expiryDate;

    public String getType() {
        return handicapType == null ? null : handicapType.getLabel();
    }

    @ToString.Include
    public String getBoat() {
        return Objects.isNull(boat) ? null : boat.getId() + " " + boat.getBoatName();
    }

}
