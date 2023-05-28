package com.sailingwebtools.marina.repository;


import com.sailingwebtools.marina.model.ChangeOwnerRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChangeOwnerRequestRepository extends JpaRepository<ChangeOwnerRequest, Long> {
}
