package au.com.ontheboat.api.repository;


import au.com.ontheboat.api.model.Boat;
import au.com.ontheboat.api.model.ChangeOwnerRequest;
import au.com.ontheboat.api.model.Crew;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@JaversSpringDataAuditable
public interface ChangeOwnerRequestRepository extends JpaRepository<ChangeOwnerRequest, Long> {
    @Query("from ChangeOwnerRequest where boat = ?1 and crew = ?2 and status in (ChangeOwnerRequestStatus.SUBMITTED, ChangeOwnerRequestStatus.IN_PROGRESS)")
    List<ChangeOwnerRequest> findAllSubmittedByUser(Boat boat, Crew crew);
}
