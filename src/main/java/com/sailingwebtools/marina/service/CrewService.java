package com.sailingwebtools.marina.service;

import com.sailingwebtools.marina.model.Crew;
import com.sailingwebtools.marina.model.dto.CrewOnboardRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CrewService {
    public UUID signOn(CrewOnboardRequest crewOnboardRequest) {
        Crew crew = Crew.builder().build();
        BeanUtils.copyProperties(crewOnboardRequest, crew);
        return UUID.randomUUID();
    }
}
