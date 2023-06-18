package au.com.ontheboat.api.controller;

import au.com.ontheboat.api.model.Crew;
import au.com.ontheboat.api.model.dto.LoginRequest;
import au.com.ontheboat.api.model.dto.SignUpRequest;
import au.com.ontheboat.api.model.dto.UserInfoResponse;
import au.com.ontheboat.api.security.JwtUtils;
import au.com.ontheboat.api.service.CrewService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private CrewService crewService;
    @Autowired
    private JwtEncoder encoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping(value = "/register", consumes = "application/json")
    public ResponseEntity<Map<String, Object>> register(@RequestBody SignUpRequest signUpRequest, HttpServletResponse response) {
        Crew crew = crewService.register(signUpRequest);
        String token = tokenValue(SecurityContextHolder.getContext().getAuthentication());
        return ResponseEntity.ok(Map.of(
                "crew", crew,
                "access_token", token,
                "message", "User registered successfully!"));
    }


    @PostMapping(value = "/signin", consumes = "application/json")
    @CrossOrigin(origins = "http://marina-ui")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Crew userDetails = (Crew) authentication.getPrincipal();

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(UserInfoResponse.builder()
                        .token(jwtUtils.generateTokenFromUsername(userDetails.getUsername()))
                        .username(userDetails.getUsername())
                        .id(userDetails.getId())
                        .email(userDetails.getEmail())
                        .roles(roles).build());
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
