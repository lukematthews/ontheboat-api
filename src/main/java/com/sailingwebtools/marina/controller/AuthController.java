package com.sailingwebtools.marina.controller;

import com.sailingwebtools.marina.model.Crew;
import com.sailingwebtools.marina.model.dto.SignUpRequest;
import com.sailingwebtools.marina.service.CrewService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private CrewService crewService;
    @Autowired
    private JwtEncoder encoder;

    @PostMapping(value = "/register", consumes = "application/json")
    public ResponseEntity<Map<String, Object>> register(@RequestBody SignUpRequest signUpRequest, HttpServletResponse response) {
        Crew crew = crewService.register(signUpRequest);
        String token = tokenValue(SecurityContextHolder.getContext().getAuthentication());
        return ResponseEntity.ok(Map.of(
                "crew", crew,
                "access_token", token,
                "message", "User registered successfully!"));
    }

    @PostMapping("/token")
    public String token(Authentication authentication, HttpServletResponse response) {
        Instant now = Instant.now();
        long expiry = 36000L;
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();
        String token = this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        Cookie jwtCookie = new Cookie("otb", token);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(36000);
        response.addCookie(jwtCookie);
        return token;
    }

    private String tokenValue(Authentication authentication) {
        Instant now = Instant.now();
        long expiry = 36000L;
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();
        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
