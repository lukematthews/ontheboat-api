package au.com.ontheboat.api.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class CrewOnboardRequest {
    private String uuid;
    @NotNull(message = "A boat to sign on to must be provided")
    private Long boatId;
    private String firstName;
    private String lastName;
    private String mobile;
    private String email;

    private LocalDate duration;
    private boolean rememberMe;
}
