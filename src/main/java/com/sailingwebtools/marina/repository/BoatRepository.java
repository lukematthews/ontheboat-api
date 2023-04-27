package com.sailingwebtools.marina.repository;


import com.sailingwebtools.marina.model.Boat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoatRepository extends JpaRepository<Boat, Long> {
    @Query(value = "from Boat where UPPER(boatName) like %:search% or UPPER(sailNumber) like %:search% or UPPER(contact) like %:search% or UPPER(design) like %:search%")
    List<Boat> findBoatByNameSailNumberContact(@Param("search") String search);
}
