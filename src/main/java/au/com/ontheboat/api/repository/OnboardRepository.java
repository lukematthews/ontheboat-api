package au.com.ontheboat.api.repository;

import au.com.ontheboat.api.model.Onboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface OnboardRepository extends JpaRepository<Onboard, Long> {
    @Query("select o from Onboard o where o.boat.id = :boat and o.timeOn = :day")
    List<Onboard> findAllByBoatAndTimeOn(@Param("boat") Long boatId, @Param("day") LocalDate day);
}
