package com.sailingwebtools.marina.controller;

import com.sailingwebtools.marina.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/admin")
@RestController
@CrossOrigin
@Slf4j
public class AdminController {
    @Autowired
    private AdminService adminService;

    @GetMapping("/migrate-contacts")
    public ResponseEntity migrateContacts() {
        adminService.migrateContacts();

        return ResponseEntity.ok().build();
    }

}
