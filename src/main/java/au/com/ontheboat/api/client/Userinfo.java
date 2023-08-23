package au.com.ontheboat.api.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Userinfo {
    private String sub; // "248289761001",
    private String name; // "Jane Josephine Doe",
    @JsonProperty("given_name")
    private String givenName; // "Jane",
    @JsonProperty("family_name")
    private String familyName; // "Doe",
    private String middleName; // "Josephine",
    private String nickname; // "JJ",
    private String preferredUsername; // "j.doe",
    private String profile; // "http://exampleco.com/janedoe",
    private String picture; // "http://exampleco.com/janedoe/me.jpg",
    private String website; // "http://exampleco.com",
    private String email; // "janedoe@exampleco.com",
    private Boolean emailVerified; // true,
    private String gender; // "female",
    private String birthdate; // "1972-03-31",
    private String zoneinfo; // "America/Los_Angeles",
    private String locale; // "en-US",
    private String phoneNumber; // "+1 (111) 222-3434",
    private String phoneNumberVerified; // false,
    private String address; // {
    //    "country; // "us"
//  },
    private String updatedAt; // "1556845729"
}
