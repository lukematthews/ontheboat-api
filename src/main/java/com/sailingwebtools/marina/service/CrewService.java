package com.sailingwebtools.marina.service;

import com.sailingwebtools.marina.model.Boat;
import com.sailingwebtools.marina.model.Crew;
import com.sailingwebtools.marina.model.Onboard;
import com.sailingwebtools.marina.model.dto.CrewOnboardRequest;
import com.sailingwebtools.marina.repository.BoatRepository;
import com.sailingwebtools.marina.repository.CrewRepository;
import com.sailingwebtools.marina.repository.OnboardRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class CrewService {

    @Autowired
    private CrewRepository crewRepository;
    @Autowired
    private BoatRepository boatRepository;
    @Autowired
    private OnboardRepository onboardRepository;

    public UUID signOn(CrewOnboardRequest crewOnboardRequest) {
        Crew crew = crewRepository.findByUuid(crewOnboardRequest.getUuid());
        if (Objects.isNull(crew)) {
            crew = Crew.builder().build();
            if (crewOnboardRequest.isRememberMe()) {
                crew.setUuid(UUID.randomUUID());
            }
            BeanUtils.copyProperties(crewOnboardRequest, crew);
            crewRepository.save(crew);
        }
        Boat boat = boatRepository.findById(crewOnboardRequest.getBoatId()).orElse(null);
        Onboard onboard = Onboard.builder()
                                 .boat(boat)
                                 .crew(crew)
                                 .build();
        onboardRepository.save(onboard);
        return crew.getUuid();
    }
}
