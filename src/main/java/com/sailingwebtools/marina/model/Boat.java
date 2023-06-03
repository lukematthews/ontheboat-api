package com.sailingwebtools.marina.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
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

import static jakarta.persistence.FetchType.EAGER;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Boat {
    @Id
    @GeneratedValue
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
    @OneToMany(cascade = CascadeType.REMOVE, fetch = EAGER)
    private List<Handicap> handicaps;
    @OneToMany(cascade = CascadeType.REMOVE, fetch = EAGER)
    @JoinColumn(name = "boat_id")
    private List<ChangeOwnerRequest> changeOwnerRequests;
    //    @ManyToMany(mappedBy = "ownedBoats", fetch = FetchType.EAGER)
    @ManyToMany(fetch = EAGER)
    @JoinTable(
            name = "boat_owners",
            joinColumns = @JoinColumn(name = "boat_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "crew_id", referencedColumnName = "id"))
    private Set<Crew> owners;
}
