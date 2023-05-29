package com.sailingwebtools.marina.service;

import com.sailingwebtools.marina.model.Boat;
import com.sailingwebtools.marina.model.ChangeOwnerRequest;
import com.sailingwebtools.marina.model.Crew;
import com.sailingwebtools.marina.model.Onboard;
import com.sailingwebtools.marina.model.dto.ChangeOwnerRequestDto;
import com.sailingwebtools.marina.model.dto.CrewOnboardRequest;
import com.sailingwebtools.marina.model.dto.CrewProfileResponse;
import com.sailingwebtools.marina.model.dto.ProfileBoatResponse;
import com.sailingwebtools.marina.model.dto.SignUpRequest;
import com.sailingwebtools.marina.model.dto.SignonDto;
import com.sailingwebtools.marina.repository.BoatRepository;
import com.sailingwebtools.marina.repository.ChangeOwnerRequestRepository;
import com.sailingwebtools.marina.repository.CrewRepository;
import com.sailingwebtools.marina.repository.OnboardRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.sailingwebtools.marina.model.dto.ChangeOwnerRequestStatus.SUBMITTED;

@Service
public class CrewService {

    @Autowired
    private CrewRepository crewRepository;
    @Autowired
    private BoatRepository boatRepository;
    @Autowired
    private OnboardRepository onboardRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ChangeOwnerRequestRepository changeOwnerRequestRepository;

    public SignonDto signOn(CrewOnboardRequest crewOnboardRequest) {

        Crew crew = null;
        boolean newCrew = false;
        if (!crewOnboardRequest.getUuid().equals("none")) {
            crew = crewRepository.findByUuid(UUID.fromString(crewOnboardRequest.getUuid()));
        }
        if (Objects.isNull(crew)) {
            newCrew = true;
            crew = Crew.builder().build();
//            if (crewOnboardRequest.isRememberMe()) {
            crew.setUuid(UUID.randomUUID());
            crew.setRoles("ROLE_USER");
//            }
            BeanUtils.copyProperties(crewOnboardRequest, crew);
            crew = crewRepository.save(crew);
        }
        Boat boat = boatRepository.findById(crewOnboardRequest.getBoatId()).orElse(null);
        Onboard onboard = Onboard.builder()
                .boat(boat)
                .crew(crew)
                .build();
        onboard = onboardRepository.save(onboard);
        return SignonDto.builder().onboard(onboard).newCrew(newCrew).build();
    }

    public Crew findCrewByUUID(String uuid) {
        return crewRepository.findByUuid(UUID.fromString(uuid));
    }

    public Crew findCrewById(Long id) {
        return crewRepository.findById(id).orElse(null);
    }

    public Crew register(SignUpRequest user) {
        Crew crew = Crew.builder().email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .roles("ROLE_USER")
                .password(passwordEncoder.encode(user.getPassword()))
                .build();
        return crewRepository.save(crew);
    }

    public CrewProfileResponse getProfile(String username) {
        Crew crew = crewRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(""));
        return CrewProfileResponse.builder()
                .id(crew.getId())
                .username(crew.getUsername())
                .firstName(crew.getFirstName())
                .lastName(crew.getLastName())
                .ownedBoats(crew.getOwnedBoats().stream().map(b -> ProfileBoatResponse.builder().boatName(b.getBoatName()).id(b.getId()).build()).toList())
                .build();
    }


    public void submitChangeOwnerRequest(String crewName, ChangeOwnerRequestDto changeOwnerRequest) throws OwnerShipChangeException {
        // is there already a request?
        Crew requestingCrew = crewRepository.findByUsername(crewName).orElseThrow(() -> new OwnerShipChangeException("Submitting crew not found"));
        Boat requestedBoat = boatRepository.findById(changeOwnerRequest.getBoatId()).orElseThrow(() -> new OwnerShipChangeException("Boat not found"));

        List<ChangeOwnerRequest> existingRequests = changeOwnerRequestRepository.findAll(Example.of(ChangeOwnerRequest.builder().boat(requestedBoat).crew(requestingCrew).build()));
        if (existingRequests.isEmpty() == false) {
            throw new OwnerShipChangeException("Ownership change request already exists");
        }
        ChangeOwnerRequest request = ChangeOwnerRequest.builder()
                .status(SUBMITTED)
                .requestType(changeOwnerRequest.getRequestType())
                .crew(requestingCrew)
                .boat(requestedBoat)
                .submitted(LocalDateTime.now())
                .lastActioned(LocalDateTime.now())
                .build();
        changeOwnerRequestRepository.save(request);
    }

//    public SignUpResponse signUp(SignUpRequest signUpRequest) {
//        Crew crew = Crew.builder().build();
//        BeanUtils.copyProperties(signUpRequest, crew);
//        crew.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
//        crew.setUuid(UUID.randomUUID());
//
//        crew = crewRepository.save(crew);
//        return SignUpResponse.builder().crew(crew).build();
//    }
}
