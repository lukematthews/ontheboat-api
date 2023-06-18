package au.com.ontheboat.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class UserInfoResponse {
    private Long id;
    private String username;
    private String email;
    private List<String> roles;

    private String token;
}
