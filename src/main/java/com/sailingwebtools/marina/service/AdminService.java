package com.sailingwebtools.marina.service;

import com.sailingwebtools.marina.model.Boat;
import com.sailingwebtools.marina.model.Crew;
import com.sailingwebtools.marina.repository.BoatRepository;
import com.sailingwebtools.marina.repository.CrewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static com.sailingwebtools.marina.model.CrewStatus.PLACEHOLDER;

@Service
@Slf4j
public class AdminService {
    @Autowired
    private BoatRepository boatRepository;
    @Autowired
    private CrewRepository crewRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    public void migrateContacts() {
        AtomicInteger counter = new AtomicInteger();
        boatRepository.findAll().stream()
                .filter(b -> hasContact(b))
                .forEach(boat -> {
                    int progress = counter.incrementAndGet();
                    if (progress % 20 == 0) {
                        log.info("migrated : {}", progress);
                    }
                    Crew placeholderOwner = Crew.builder()
                            .username(boat.getContact())
                            .firstName(boat.getContact())
                            .ownedBoats(Set.of(boat))
                            .password(passwordEncoder.encode(boat.getBoatName()))
                            .status(PLACEHOLDER)
                            .roles("ROLE_USER")
                            .build();
                    crewRepository.save(placeholderOwner);
//                    boat.getOwners().add(placeholderOwner);
//                    boatRepository.save(boat);
                });
    }

    private static boolean hasContact(Boat b) {
        // is there a contact? (not null and not an empty string)
        // are there already owners?
        return b.getContact() != null &&
                b.getContact().trim().length() > 0
                && b.getOwners().isEmpty();
    }
}
