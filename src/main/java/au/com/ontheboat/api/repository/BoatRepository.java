package au.com.ontheboat.api.repository;


import au.com.ontheboat.api.model.Boat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import static au.com.ontheboat.api.repository.SearchConstants.LIKE_CLAUSE;

public interface BoatRepository extends JpaRepository<Boat, Long> {
    @Query(value = "from Boat where UPPER(boatName) like %:search% or UPPER(sailNumber) like %:search% or UPPER(contact) like %:search% or UPPER(design) like %:search%")
    List<Boat> findBoatByNameSailNumberContact(@Param("search") String search);

    @Query("SELECT b FROM Boat b WHERE " +
            "LOWER(b.boatName) LIKE " + LIKE_CLAUSE + " OR " +
            "LOWER(b.sailNumber) LIKE " + LIKE_CLAUSE + " OR " +
            "LOWER(b.contact) LIKE " + LIKE_CLAUSE + " OR " +
            "LOWER(b.design) LIKE " + LIKE_CLAUSE)
    Page<Boat> findBySearchTerm(@Param("searchTerm") String searchTerm,
            Pageable pageRequest);

    @Query
    Boat findByExternalId(String externalId);
}
