package com.sailingwebtools.marina.repository;

import com.sailingwebtools.marina.model.Crew;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CrewRepository extends JpaRepository<Crew, Long> {
    Crew findByUuid(UUID uuid);

    Optional<Crew> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Optional<Crew> findByEmail(String email);
}
