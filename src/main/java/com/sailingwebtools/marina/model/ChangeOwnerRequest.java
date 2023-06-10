package com.sailingwebtools.marina.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sailingwebtools.marina.model.dto.ChangeOwnerRequestStatus;
import com.sailingwebtools.marina.model.dto.ChangeOwnerRequestType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ChangeOwnerRequest {
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "boat_id", insertable = false, updatable = false)
    private Boat boat;
    @ManyToOne
    @JoinColumn(name = "crew_id", insertable = false, updatable = false)
    private Crew crew;
    @Column
    private ChangeOwnerRequestType requestType;
    @Column
    private ChangeOwnerRequestStatus status;
    @Column
    private LocalDateTime submitted;
    @Column
    private LocalDateTime lastActioned;
}