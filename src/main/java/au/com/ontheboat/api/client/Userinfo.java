package au.com.ontheboat.api.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Userinfo {
    private String sub;//": "auth0|64d814b1669cca90bd07f46c",
    private String nickname;//": "matthews.luke.c",
    private String name;//": "matthews.luke.c@gmail.com",
    private String picture;//": "https://s.gravatar.com/avatar/c22d152013184d1bc19c1f43ade4be91?s=480&r=pg&d=https%3A%2F%2Fcdn.auth0.com%2Favatars%2Fma.png",
    private String updated_at;//": "2023-08-21T06:34:15.354Z",
    private String email;//": "matthews.luke.c@gmail.com",
    private Boolean email_verified;//": false
}
