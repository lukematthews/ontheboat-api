package au.com.ontheboat.api.repository;


import au.com.ontheboat.api.model.BoatMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface BoatMediaRepository extends JpaRepository<BoatMedia, Long> {
    @Modifying
    @Query
    void deleteByFileId(String fileId);
}
