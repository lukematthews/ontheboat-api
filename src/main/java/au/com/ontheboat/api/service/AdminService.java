package au.com.ontheboat.api.service;

import au.com.ontheboat.api.model.Boat;
import au.com.ontheboat.api.model.Crew;
import au.com.ontheboat.api.model.CrewStatus;
import au.com.ontheboat.api.repository.BoatRepository;
import au.com.ontheboat.api.repository.CrewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
        List<Boat> boatList = boatRepository.findAll();
        boatList.stream().map(Boat::getContact).collect(Collectors.toSet());
        Map<String, Integer> usernameCounts = new HashMap<>();
        boatList.stream()
                .filter(b -> hasContact(b))
                .forEach(boat -> {
                    int progress = counter.incrementAndGet();
                    if (progress % 20 == 0) {
                        log.info("migrated : {}", progress);
                    }
                    String username = boat.getContact();
                    Integer usernameCount = usernameCounts.get(username);
                    if (usernameCount == null) {
                        usernameCounts.put(username, 1);
                    } else {
                        username = username + "-" + usernameCount;
                        usernameCount++;
                        usernameCounts.put(boat.getContact(), usernameCount);
                    }
                    Set<Boat> ownedBoats = new HashSet<>();
                    ownedBoats.add(boat);
                    Crew placeholderOwner = Crew.builder()
                            .username(username.toLowerCase().replaceAll(" ", ""))
                            .firstName(boat.getContact())
                            .ownedBoats(ownedBoats)
                            .password(passwordEncoder.encode(boat.getBoatName()))
                            .status(CrewStatus.PLACEHOLDER)
                            .roles("ROLE_USER")
                            .build();
                    crewRepository.save(placeholderOwner);
                    if (boat.getOwners() != null) {
                        boat.getOwners().add(placeholderOwner);
                    } else {
                        Set<Crew> owners = new HashSet<>();
                        owners.add(placeholderOwner);
                        boat.setOwners(owners);
                    }
                });
    }

    public List<Boat> noOwners() {
        List<Boat> noOwners = boatRepository.findAll().stream()
                .filter(b -> b.getOwners().size() == 0).collect(Collectors.toList());
        log.info("No owners? {}", noOwners.size());
        Progress progress = Progress.builder()
                .total(noOwners.size())
                .increment(20)
                .build();
        noOwners.stream().forEach(b -> {
            Set<Crew> crewForBoatContact = crewRepository.findByFirstName(b.getContact());
            List<Crew> potentialOwners = crewForBoatContact.stream().filter(c -> c.getOwnedBoats().contains(b)).collect(Collectors.toList());
            b.getOwners().addAll(potentialOwners);
            boatRepository.saveAndFlush(b);
            progress.increment();
        });
        return noOwners;
    }

    private static boolean hasContact(Boat b) {
        // is there a contact? (not null and not an empty string)
        // are there already owners?
        return b.getContact() != null &&
                b.getContact().trim().length() > 0
                && b.getOwners().isEmpty();
    }
}
