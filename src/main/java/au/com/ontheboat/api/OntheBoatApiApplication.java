package au.com.ontheboat.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
//@EnableMethodSecurity(securedEnabled = true)
@EnableWebSecurity
public class OntheBoatApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(OntheBoatApiApplication.class, args);
    }

}
