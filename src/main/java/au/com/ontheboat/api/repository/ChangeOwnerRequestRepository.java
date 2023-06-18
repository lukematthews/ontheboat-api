package au.com.ontheboat.api.repository;


import au.com.ontheboat.api.model.ChangeOwnerRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChangeOwnerRequestRepository extends JpaRepository<ChangeOwnerRequest, Long> {
}
