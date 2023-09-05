package au.com.ontheboat.api.model;

import au.com.ontheboat.api.model.dto.ChangeOwnerRequestStatus;
import au.com.ontheboat.api.model.dto.ChangeOwnerRequestType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ChangeOwnerRequest {
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Boat boat;
    @ManyToOne
    private Crew crew;
    @Column
    private ChangeOwnerRequestType requestType;
    @Column
    private ChangeOwnerRequestStatus status;
    @Column
    private LocalDateTime submitted;
    @Column
    private LocalDateTime lastActioned;
}
