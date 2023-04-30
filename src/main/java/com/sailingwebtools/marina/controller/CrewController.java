package com.sailingwebtools.marina.controller;

import com.sailingwebtools.marina.model.dto.CrewOnboardRequest;
import com.sailingwebtools.marina.service.CrewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/crew")
public class CrewController {
    @Autowired
    private CrewService crewService;

    @PostMapping("/sign-on")
    public String signOn(@RequestBody CrewOnboardRequest crew) {
        return crewService.signOn(crew).toString();
    }
}
