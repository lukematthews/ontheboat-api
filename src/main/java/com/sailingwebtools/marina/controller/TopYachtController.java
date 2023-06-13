package com.sailingwebtools.marina.controller;

import com.sailingwebtools.marina.model.Boat;
import com.sailingwebtools.marina.service.TopYachtLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/top-yacht")
@Slf4j
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class TopYachtController {
    @Autowired
    private TopYachtLoader topYachtLoader;

    @GetMapping("/boats-from-partner")
    public ResponseEntity<List<Boat>> getAllBoats() {
        return ResponseEntity.ok(topYachtLoader.loadFromTopYacht());
    }

    @GetMapping("/update-archived-state-from-partner")
    public ResponseEntity<List<Boat>> updateArchivedState() {
        return ResponseEntity.ok(topYachtLoader.updateArchivedStateFromTopYacht());
    }

    @GetMapping("/save-image")
    public ResponseEntity fetchBoatImage(@RequestParam Long boatId) {
        topYachtLoader.saveImage(boatId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/save-all-images")
    public ResponseEntity fetchBoatImages() {
        topYachtLoader.saveAllImages();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/boat-details")
    @CrossOrigin(origins = "http://marina-ui")
    public ResponseEntity<Boat> getBoatDetails(@RequestParam Long boatId) {
        log.info("/api/boat-details?boatId={}", boatId);
        return ResponseEntity.ok(topYachtLoader.getBoatDetails(boatId));
    }
}
