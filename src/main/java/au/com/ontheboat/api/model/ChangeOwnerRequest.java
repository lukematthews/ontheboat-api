package au.com.ontheboat.api.model;

import au.com.ontheboat.api.model.dto.ChangeOwnerRequestStatus;
import au.com.ontheboat.api.model.dto.ChangeOwnerRequestType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EntityListeners(AuditingEntityListener.class)
public class ChangeOwnerRequest {
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @ToString.Exclude
    @JsonBackReference
    private Boat boat;
    @ManyToOne
    @ToString.Exclude
    @JsonBackReference
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
