package au.com.ontheboat.api.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignUpRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private String password;
    private String username;
}
