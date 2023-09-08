package au.com.ontheboat.api.controller;

import au.com.ontheboat.api.model.dto.ChangeOwnerRequestDetailDto;
import au.com.ontheboat.api.model.dto.ChangeOwnerRequestDto;
import au.com.ontheboat.api.model.dto.CrewOnboardRequest;
import au.com.ontheboat.api.model.dto.CrewProfileResponse;
import au.com.ontheboat.api.model.dto.OnboardResponse;
import au.com.ontheboat.api.model.dto.OwnershipChangeAction;
import au.com.ontheboat.api.model.dto.SignonDto;
import au.com.ontheboat.api.service.CrewService;
import au.com.ontheboat.api.service.OwnerShipChangeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RequestMapping("/crew")
@RestController
@CrossOrigin
@Slf4j
public class CrewController {
    @Autowired
    private CrewService crewService;

    @PostMapping("/sign-on")
    public ResponseEntity<SignonDto> signOn(@RequestBody @Validated CrewOnboardRequest crew, @CookieValue(name = "crewUUID", defaultValue = "none", required = false) String crewUUID, @CookieValue(name = "cookiesEnabled", defaultValue = "true") String cookiesEnabled) {
        log.info(crewUUID);
        SignonDto onboardCrew = crewService.signOn(crew);
        HttpHeaders headers = new HttpHeaders();
        if (cookiesEnabled.equals("true")) {
            headers.add("Set-Cookie", String.format("crewUUID=%s; Max-Age=604800; Path=/;", onboardCrew.getOnboard().getCrew().getUuid()));
            headers.add("Set-Cookie", String.format("lastBoatOnboard=%s; Max-Age=604800; Path=/;", onboardCrew.getOnboard().getBoat().getId()));
        }
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(onboardCrew);
    }

    @GetMapping("/find-by-id")
    public ResponseEntity<CrewProfileResponse> getCrewForUUID(@RequestParam String uuid) {
        CrewProfileResponse crew = crewService.findCrewByUUID(uuid);
        if (crew == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(crew);
    }

    @GetMapping("/profile")
    public ResponseEntity<CrewProfileResponse> getProfileForUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            CrewProfileResponse profileResponse = crewService.getProfile(authentication.getName());

            return ResponseEntity.ok(profileResponse);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.ok(crewService.getNotSignedUp(authentication));
        }
    }

    @GetMapping("/ownership-requests")
    public ResponseEntity<List<ChangeOwnerRequestDetailDto>> getOwnershipChangeRequests(Authentication authentication) {
        List<ChangeOwnerRequestDetailDto> submittedRequests = crewService.findAllOwnershipRequestsLodgedBy(authentication.getName());
        return ResponseEntity.ok(submittedRequests);
    }

    @GetMapping("/all-ownership-requests")
    @PreAuthorize("hasAuthority('read:admin')")
    public ResponseEntity<List<ChangeOwnerRequestDetailDto>> getallOwnershipChangeRequests(Authentication authentication) {
        List<ChangeOwnerRequestDetailDto> allRequests = crewService.findAllOwnershipRequests();
        return ResponseEntity.ok(allRequests);
    }

    @PostMapping(value = "/request-ownership-change", consumes = "application/json")
    public ResponseEntity changeOwner(Authentication authentication, @RequestBody ChangeOwnerRequestDto changeOwnerRequest) {
        try {
            crewService.submitChangeOwnerRequest(authentication.getName(), changeOwnerRequest);
            return ResponseEntity.ok().build();
        } catch (OwnerShipChangeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "/action-ownership-change", consumes = "application/json")
    public ResponseEntity actionOwnershipChange(Authentication authentication, @RequestBody OwnershipChangeAction ownershipChangeAction) {
        crewService.performOwnershipChangeAction(authentication.getName(), ownershipChangeAction);
        return ResponseEntity.ok("");
    }

    @PutMapping(value = "/profile", consumes = "application/json")
    public ResponseEntity<CrewProfileResponse> updateUser(@RequestBody CrewProfileResponse crewProfileResponse) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CrewProfileResponse response = crewService.save(authentication, crewProfileResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/onboard")
    public ResponseEntity<List<OnboardResponse>> onboardRequest(@RequestParam Long boatId, @RequestParam LocalDate day) {
        List<OnboardResponse> onboardResponseList = crewService.onboardListing(boatId, day);
        return ResponseEntity.ok(onboardResponseList);
    }
}
