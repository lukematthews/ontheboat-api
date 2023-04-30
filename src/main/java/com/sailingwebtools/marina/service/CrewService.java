package com.sailingwebtools.marina.service;

import com.sailingwebtools.marina.model.dto.CrewOnboardRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CrewService {
    public UUID signOn(CrewOnboardRequest crewOnboardRequest) {
        return UUID.randomUUID();
    }
}
