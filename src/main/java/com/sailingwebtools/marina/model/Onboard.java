package com.sailingwebtools.marina.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
public class Onboard {
    @Column
    private Boat boat;
    @Column
    private Crew crew;
    @Column
    private LocalDateTime timeOn;
    @Column
    private Long duration;
}
