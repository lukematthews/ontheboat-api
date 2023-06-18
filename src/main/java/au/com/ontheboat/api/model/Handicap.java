package au.com.ontheboat.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Handicap {
    @Id
    @GeneratedValue
    private Long id;
    @Column
    @JsonIgnore
    private HandicapType handicapType;
    @Column
    private String rating;
    @Column
    private String certificate;
    @Column
    private LocalDate expiryDate;

    public String getType() {
        return handicapType == null ? null : handicapType.getLabel();
    }
}
