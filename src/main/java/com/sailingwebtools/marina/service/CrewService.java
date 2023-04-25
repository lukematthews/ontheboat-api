package com.sailingwebtools.marina.service;

import com.sailingwebtools.marina.model.Crew;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CrewService {
    public UUID signOn(Long boatId, Crew crew) {
        return UUID.randomUUID();
    }
}
