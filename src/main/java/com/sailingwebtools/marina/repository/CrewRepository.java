package com.sailingwebtools.marina.repository;

import com.sailingwebtools.marina.model.Crew;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrewRepository extends JpaRepository<Crew, Long> {
}
