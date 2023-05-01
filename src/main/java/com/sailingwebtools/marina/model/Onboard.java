package com.sailingwebtools.marina.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
public class Onboard {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Boat boat;
    @ManyToOne
    private Crew crew;
    @Column
    private LocalDateTime timeOn;
    @Column
    private Long duration;
}
