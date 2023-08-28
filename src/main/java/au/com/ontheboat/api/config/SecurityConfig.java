package au.com.ontheboat.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Value("${okta.oauth2.issuer}")
    private String issuer;
    @Value("${okta.oauth2.client-id}")
    private String clientId;

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    LogoutHandler oidcLogoutHandler() {
        return (request, response, authentication) -> {
            try {
                response.sendRedirect(issuer + "v2/logout?client_id=" + clientId + "&returnTo=http://localhost:8080/");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {

        http.csrf((csrf) -> csrf
                .ignoringRequestMatchers("/marina/**")
        );
        http.cors(cors -> cors.disable());
        http.authorizeHttpRequests((requests) -> requests
                .requestMatchers(AntPathRequestMatcher.antMatcher("/marina/**")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/")).permitAll()
                .anyRequest().authenticated());

        // configure logout handler
        http.logout(logout -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .addLogoutHandler(oidcLogoutHandler()));
        // enable OAuth2/OIDC
        http.oauth2Login(withDefaults());
        http.oauth2ResourceServer(oauth -> oauth.jwt(withDefaults()));

        return http.build();
    }
}