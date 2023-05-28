package com.sailingwebtools.marina.controller;

import com.sailingwebtools.marina.model.Crew;
import com.sailingwebtools.marina.model.dto.ChangeOwnerRequestDto;
import com.sailingwebtools.marina.model.dto.CrewOnboardRequest;
import com.sailingwebtools.marina.model.dto.CrewProfileResponse;
import com.sailingwebtools.marina.model.dto.SignonDto;
import com.sailingwebtools.marina.service.CrewService;
import com.sailingwebtools.marina.service.OwnerShipChangeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        if (crew.isRememberMe() && cookiesEnabled.equals("true")) {
            headers.add("Set-Cookie", String.format("crewUUID=%s; Max-Age=604800; Path=/;", onboardCrew.getOnboard().getCrew().getUuid()));
            headers.add("Set-Cookie", String.format("lastBoatOnboard=%s; Max-Age=604800; Path=/;", onboardCrew.getOnboard().getBoat().getId()));
        }
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(onboardCrew);
    }

    @GetMapping("/find-by-id")
    public ResponseEntity<Crew> getCrewForUUID(@RequestParam String uuid) {
        Crew crew = crewService.findCrewByUUID(uuid);
        if (crew == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(crew);
    }

    @GetMapping("/profile")
//    @PreAuthorize("hasRole('ROLE_CREW')")
    public ResponseEntity<CrewProfileResponse> getProfileForUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CrewProfileResponse profileResponse = crewService.getProfile(authentication.getName());

        return ResponseEntity.ok(profileResponse);
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
}
