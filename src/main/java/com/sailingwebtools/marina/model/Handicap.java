package com.sailingwebtools.marina.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
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
    private Boat boat;
    @Column
    private HandicapType handicapType;
    @Column
    private String rating;
    @Column
    private String certificate;
    @Column
    private LocalDate expiryDate;

    @ToString.Include
    public String getBoat() {
        return Objects.isNull(boat) ? null : boat.getId() + " " + boat.getBoatName();
    }

}
