package com.sailingwebtools.marina.service;

import com.sailingwebtools.marina.model.Boat;
import com.sailingwebtools.marina.repository.BoatRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class BoatService {
    @Autowired
    private BoatRepository boatRepository;

    public Page<Boat> getAllBoats(Pageable page) {
        log.info(page.toString());
        return boatRepository.findAll(Example.of(Boat.builder().archived(false).build()), page);
    }

    public List<Boat> findBoats(String search) {
        return boatRepository.findBoatByNameSailNumberContact(search == null ? "" : search.toUpperCase());
    }

    public Page<Boat> findBoats(String search, Pageable pageable) {
        return boatRepository.findBySearchTerm(search, pageable);
    }

    public Boat getBoatDetails(Long boatId) {
        return boatRepository.findById(boatId).orElse(null);
    }

}
