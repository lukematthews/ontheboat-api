package com.sailingwebtools.marina.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class SignUpRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private String password;
    private String username;
    private Set<String> role;
}
