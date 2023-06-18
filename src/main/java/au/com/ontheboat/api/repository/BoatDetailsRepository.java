package au.com.ontheboat.api.repository;


import au.com.ontheboat.api.model.BoatDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoatDetailsRepository extends JpaRepository<BoatDetails, Long> {
}
