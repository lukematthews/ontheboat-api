package au.com.ontheboat.api.repository;

import au.com.ontheboat.api.model.Crew;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface CrewRepository extends JpaRepository<Crew, Long> {
    Crew findByUuid(UUID uuid);

    Optional<Crew> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Optional<Crew> findByEmail(String email);

    Set<Crew> findByFirstName(String firstName);

    @Query(value = "select c.username, count(*) from Crew c group by c.username having count(*) > 1", nativeQuery = true)
    List<Object[]> findDuplicates();

    Set<Crew> findByUuidIsNull();
}
