package com.sailingwebtools.marina.controller;

import com.sailingwebtools.marina.model.Crew;
import com.sailingwebtools.marina.model.dto.SignUpRequest;
import com.sailingwebtools.marina.service.CrewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private CrewService crewService;

    @PostMapping(value = "/register", consumes = "application/json")
    public ResponseEntity<Crew> register(@RequestBody SignUpRequest signUpRequest) {
        Crew crew = crewService.register(signUpRequest);
        return ResponseEntity.ok(crew);
    }
}
