package com.sailingwebtools.marina.controller;

import com.sailingwebtools.marina.model.Crew;
import com.sailingwebtools.marina.repository.BoatRepository;
import com.sailingwebtools.marina.repository.CrewRepository;
import com.sailingwebtools.marina.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RequestMapping("/admin")
@RestController
@CrossOrigin
@Slf4j
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private CrewRepository crewRepository;
    @Autowired
    private BoatRepository boatRepository;

    @GetMapping("/migrate-contacts")
    public ResponseEntity migrateContacts() {
        adminService.migrateContacts();

        return ResponseEntity.ok().build();
    }

    @GetMapping("/manage-duplicates")
    public ResponseEntity manageDuplicates() {
        List<Object[]> duplicates = crewRepository.findDuplicates();
        duplicates.stream().forEach(d -> log.info("username: {}, count: {}", d[0], d[1]));
        duplicates.stream().forEach(d -> {
            String username = d[0].toString();
            List<Crew> duplicatesForUsername = crewRepository.findAll(Example.of(Crew.builder().username(username).build()));
            AtomicInteger count = new AtomicInteger();
            duplicatesForUsername.stream().forEach(dupe -> {
                dupe.setUsername(dupe.getUsername() + "-" + count.incrementAndGet());
                log.info(dupe.getUsername());
                crewRepository.save(dupe);
            });
        });
        return ResponseEntity.ok().build();
    }

    @GetMapping("/fix-duplicate-mapping")
    public ResponseEntity fixDuplicates() {
        List<Crew> crewList = crewRepository.findAll().stream().filter(c -> c.getUsername().matches(".*-[0-9]+")).collect(Collectors.toList());
        crewList.stream().forEach(c -> {
            c.getOwnedBoats().stream()
                    .filter(b -> b.getOwners().size() == 0)
                    .forEach(b -> {
                        b.getOwners().add(c);
                        boatRepository.save(b);
                        log.info("added owner to boat: {}", b.getBoatName());
                    });
        });
        return ResponseEntity.ok().build();
    }

    @GetMapping("/no-owners")
    public ResponseEntity noOwners() {
        return ResponseEntity.ok(adminService.noOwners());
    }
}
