package au.com.ontheboat.api.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UsersRequest {
    private String first_name;
    private String last_name;
    private String email;
    private String password;
    private String roles;
}