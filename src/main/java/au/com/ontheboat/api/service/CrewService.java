package au.com.ontheboat.api.service;

import au.com.ontheboat.api.client.Auth0Client;
import au.com.ontheboat.api.client.Userinfo;
import au.com.ontheboat.api.model.Boat;
import au.com.ontheboat.api.model.ChangeOwnerRequest;
import au.com.ontheboat.api.model.Crew;
import au.com.ontheboat.api.model.Onboard;
import au.com.ontheboat.api.model.dto.ChangeOwnerRequestDetailDto;
import au.com.ontheboat.api.model.dto.ChangeOwnerRequestDto;
import au.com.ontheboat.api.model.dto.ChangeOwnerRequestStatus;
import au.com.ontheboat.api.model.dto.CrewOnboardRequest;
import au.com.ontheboat.api.model.dto.CrewProfileResponse;
import au.com.ontheboat.api.model.dto.OnboardResponse;
import au.com.ontheboat.api.model.dto.OwnershipChangeAction;
import au.com.ontheboat.api.model.dto.ProfileBoatResponse;
import au.com.ontheboat.api.model.dto.SignUpRequest;
import au.com.ontheboat.api.model.dto.SignonDto;
import au.com.ontheboat.api.repository.BoatRepository;
import au.com.ontheboat.api.repository.ChangeOwnerRequestRepository;
import au.com.ontheboat.api.repository.CrewRepository;
import au.com.ontheboat.api.repository.OnboardRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static au.com.ontheboat.api.model.CrewStatus.ACTIVE;
import static au.com.ontheboat.api.model.CrewStatus.PLACEHOLDER;
import static au.com.ontheboat.api.model.dto.ChangeOwnerRequestStatus.APPROVED;
import static au.com.ontheboat.api.model.dto.ChangeOwnerRequestStatus.SUBMITTED;

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
    @Autowired
    private Auth0Client auth0Client;

    public void populateUUID() {
        Set<Crew> uuidIsNull = crewRepository.findByUuidIsNull();
        Progress p = Progress.builder().total(uuidIsNull.size()).build();
        uuidIsNull.stream().forEach(c -> {
            c.setUuid(UUID.randomUUID());
            crewRepository.save(c);
            p.increment();
        });
    }

    public List<OnboardResponse> onboardListing(Long boatId, LocalDate day) {
        List<Onboard> onboard = onboardRepository.findAllByBoatAndTimeOn(boatId, day);
        return onboard.stream()
                .map(o -> OnboardResponse.builder()
                        .uuid(o.getCrew().getUuid())
                        .name(o.getCrew().getFirstName() + " " + o.getCrew().getLastName())
                        .boatId(boatId)
                        .day(day)
                        .build())
                .collect(Collectors.toList());
    }

    public SignonDto signOn(CrewOnboardRequest crewOnboardRequest) {

        Crew crew = null;
        boolean newCrew = false;
        if (!Objects.isNull(crewOnboardRequest.getUuid()) && !crewOnboardRequest.getUuid().equals("none")) {
            crew = crewRepository.findByUuid(UUID.fromString(crewOnboardRequest.getUuid()));
        }
        if (Objects.isNull(crew)) {
            newCrew = true;
            crew = Crew.builder().build();
            crew.setUuid(UUID.randomUUID());
            crew.setRoles("ROLE_USER");
            BeanUtils.copyProperties(crewOnboardRequest, crew);
            crew = crewRepository.save(crew);
        }
        Boat boat = boatRepository.findById(crewOnboardRequest.getBoatId()).orElse(null);
        Onboard onboard = Onboard.builder()
                .boat(boat)
                .crew(crew)
                .timeOn(crewOnboardRequest.getDuration())
                .build();
        onboard = onboardRepository.save(onboard);
        return SignonDto.builder().onboard(onboard).newCrew(newCrew).build();
    }

    public CrewProfileResponse findCrewByUUID(String uuid) {
        Crew crew = crewRepository.findByUuid(UUID.fromString(uuid));
        return getProfile(crew.getUsername());
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
        Crew crew = crewRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username + "is not registered"));
        return CrewProfileResponse.builder()
                .id(crew.getId())
                .username(crew.getUsername())
                .firstName(crew.getFirstName())
                .lastName(crew.getLastName())
                .mobile(crew.getMobile())
                .email(crew.getEmail())
                .ownedBoats(crew.getOwnedBoats().stream().map(b -> ProfileBoatResponse.builder().boatName(b.getBoatName()).id(b.getId()).build()).toList())
                .status(crew.getStatus())
                .build();
    }

    public CrewProfileResponse getNotSignedUp(Authentication authentication) {
        String token = ((JwtAuthenticationToken) authentication).getToken().getTokenValue();
        Userinfo userInfo = auth0Client.getUserInfo("Bearer " + token);
        return CrewProfileResponse.builder()
                .id(-1L)
                .email(userInfo.getEmail())
                .firstName(userInfo.getGivenName())
                .lastName(userInfo.getFamilyName())
                .mobile(userInfo.getPhoneNumber())
                .status(PLACEHOLDER)
                .ownedBoats(List.of())
                .build();
    }

    public void submitChangeOwnerRequest(String crewName, ChangeOwnerRequestDto changeOwnerRequest) throws OwnerShipChangeException {
        // is there already a request?
        Crew requestingCrew = crewRepository.findByUsername(crewName).orElseThrow(() -> new OwnerShipChangeException("Submitting crew not found"));
        Boat requestedBoat = boatRepository.findById(changeOwnerRequest.getBoatId()).orElseThrow(() -> new OwnerShipChangeException("Boat not found"));

        List<ChangeOwnerRequest> existingRequests = changeOwnerRequestRepository.findAllSubmittedByUser(requestedBoat, requestingCrew);
        if (existingRequests.isEmpty() == false) {
            throw new OwnerShipChangeException("Ownership change request already exists.");
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

    public CrewProfileResponse save(Authentication authentication, CrewProfileResponse crewProfileResponse) {
        if (crewProfileResponse.getStatus() == PLACEHOLDER) {
            Crew crew = Crew.builder()
                    .build();
            BeanUtils.copyProperties(crewProfileResponse, crew);
            crew.setUsername(authentication.getName());
            crew.setStatus(ACTIVE);
            crew.setRoles("USER");
            Crew savedCrew = crewRepository.save(crew);
            BeanUtils.copyProperties(savedCrew, crewProfileResponse);
        } else {
            Crew crew = crewRepository.findByUsername(crewProfileResponse.getUsername()).orElseThrow();
            BeanUtils.copyProperties(crewProfileResponse, crew, "status", "ownedBoats", "username", "id");
            crew = crewRepository.save(crew);
            BeanUtils.copyProperties(crew, crewProfileResponse);
        }
        return crewProfileResponse;
    }

    public List<ChangeOwnerRequestDetailDto> findAllOwnershipRequestsLodgedBy(String name) {
        Crew crew = crewRepository.findByUsername(name).orElseThrow();
        return crew.getChangeOwnerRequests().stream().map(request -> ChangeOwnerRequestDetailDto.builder()
                        .boatName(request.getBoat().getBoatName())
                        .status(request.getStatus())
                        .submitted(request.getSubmitted().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).build())
                .collect(Collectors.toList());
    }

    public List<ChangeOwnerRequestDetailDto> findAllOwnershipRequests() {
        return changeOwnerRequestRepository.findAll(Sort.by(Sort.Direction.DESC, "submitted")).stream()
                .map(request -> ChangeOwnerRequestDetailDto.builder()
                        .id(request.getId())
                        .boatName(request.getBoat() != null ? request.getBoat().getBoatName() : "")
                        .status(request.getStatus())
                        .submitted(request.getSubmitted().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).build())
                .collect(Collectors.toList());
    }

    public void performOwnershipChangeAction(String name, OwnershipChangeAction ownershipChangeAction) {
        ChangeOwnerRequest changeOwnerRequest = changeOwnerRequestRepository.findById(ownershipChangeAction.getId()).get();
        switch (ownershipChangeAction.getAction()) {
            case APPROVED -> performApprovedRequest(changeOwnerRequest);
            case DECLINED -> performDeclineRequest(changeOwnerRequest);
            case CANCELLED -> performCancelRequest(changeOwnerRequest);
        }
    }

    private void performCancelRequest(ChangeOwnerRequest changeOwnerRequest) {
        changeOwnerRequest.setStatus(ChangeOwnerRequestStatus.CANCELLED);
        changeOwnerRequestRepository.save(changeOwnerRequest);
    }

    private void performDeclineRequest(ChangeOwnerRequest changeOwnerRequest) {
        changeOwnerRequest.setStatus(ChangeOwnerRequestStatus.REJECTED);
        changeOwnerRequestRepository.save(changeOwnerRequest);
    }

    private void performApprovedRequest(ChangeOwnerRequest changeOwnerRequest) {
        Boat boat = changeOwnerRequest.getBoat();
        Set<Crew> owners = boat.getOwners();
        String description = null;
        switch (changeOwnerRequest.getRequestType()) {
            case REMOVE:
                owners.remove(changeOwnerRequest.getCrew());
                description = String.format("Removed %s as an owner of %s", changeOwnerRequest.getCrew().getFirstName() + " " + changeOwnerRequest.getCrew().getLastName(), boat.getBoatName());
                break;
            case PARTNER:
                owners.add(changeOwnerRequest.getCrew());
                description = String.format("Added %s as an owner of %s", changeOwnerRequest.getCrew().getFirstName() + " " + changeOwnerRequest.getCrew().getLastName(), boat.getBoatName());
                break;
            case SOLE_OWNER:
                owners.clear();
                owners.add(changeOwnerRequest.getCrew());
                description = String.format("%s is the sole owner of %s", changeOwnerRequest.getCrew().getFirstName() + " " + changeOwnerRequest.getCrew().getLastName(), boat.getBoatName());
                break;
        }
        boatRepository.save(boat);
        changeOwnerRequest.setStatus(APPROVED);
        changeOwnerRequestRepository.save(changeOwnerRequest);
    }
}
