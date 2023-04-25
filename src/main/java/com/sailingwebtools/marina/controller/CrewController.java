package com.sailingwebtools.marina.controller;

import com.sailingwebtools.marina.model.Crew;
import com.sailingwebtools.marina.service.CrewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/crew")
public class CrewController {
    @Autowired
    private CrewService crewService;

    @PostMapping("/sign-on")
    public String signOn(@RequestParam Long boatId, @RequestBody Crew crew) {
        return crewService.signOn(boatId, crew).toString();
    }
}
