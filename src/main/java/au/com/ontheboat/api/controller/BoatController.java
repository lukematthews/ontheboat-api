package au.com.ontheboat.api.controller;

import au.com.ontheboat.api.model.dto.BoatUpdateDto;
import au.com.ontheboat.api.service.BoatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/boat")
@CrossOrigin
@Slf4j
public class BoatController {
    @Autowired
    private BoatService boatService;

    @PutMapping("/update-boat-details")
    public ResponseEntity update(Authentication authentication, @RequestBody BoatUpdateDto boat) {
        // check that the authenticated user is an owner.
        if (!boatService.isCrewOwner(boat, authentication.getName())) {
            return new ResponseEntity<>("user does not own boat", HttpStatus.FORBIDDEN);
        }
        boatService.updateBoat(boat);
        return ResponseEntity.ok().build();
    }
}
