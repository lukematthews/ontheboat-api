package com.sailingwebtools.marina.repository;


import com.sailingwebtools.marina.model.Boat;
import com.sailingwebtools.marina.model.dto.BoatSearchResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoatRepository extends JpaRepository<Boat, Long> {
    @Query(value = "select NEW com.sailingwebtools.marina.model.dto.BoatSearchResult(id, boatName, sailNumber) from Boat where UPPER(boatName) like %:search% or UPPER(sailNumber) like %:search% or UPPER(contact) like %:search% or UPPER(design) like %:search%")
    List<BoatSearchResult> findBoatByNameSailNumberContact(@Param("search") String search);
}
