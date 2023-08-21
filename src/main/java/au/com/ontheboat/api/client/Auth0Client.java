package au.com.ontheboat.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "Auth0", url = "${okta.oauth2.issuer}")
public interface Auth0Client {
    @RequestMapping("/userinfo")
    Userinfo getUserInfo(@RequestHeader("Authorization") String token);
}
