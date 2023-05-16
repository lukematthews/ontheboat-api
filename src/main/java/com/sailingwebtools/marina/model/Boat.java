package com.sailingwebtools.marina.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Boat {
    @Id
    private Long id;
    @Column
    private String boatName;
    @Column
    private String sailNumber;
    @Column
    private String contact;
    @Column
    private String design;
    @Column
    private Boolean archived;
    @OneToOne(cascade = CascadeType.REMOVE)
    private BoatDetails boatDetails;
    @OneToMany(cascade = CascadeType.REMOVE)
    private List<Handicap> handicaps;
    @ManyToMany
    @JoinTable(name = "boat_owners",
            joinColumns = @JoinColumn(name = "boat_id"),
            inverseJoinColumns = @JoinColumn(name = "owners_id"))
    private Set<Crew> owners;
}
