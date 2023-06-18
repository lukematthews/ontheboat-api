package au.com.ontheboat.api.repository;


import au.com.ontheboat.api.model.Handicap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HandicapRepository extends JpaRepository<Handicap, Long> {

}
