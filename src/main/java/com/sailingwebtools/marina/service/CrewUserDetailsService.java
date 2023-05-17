package com.sailingwebtools.marina.service;

import com.sailingwebtools.marina.repository.CrewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CrewUserDetailsService implements UserDetailsService {
    @Autowired
    private CrewRepository crewRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return crewRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(""));
    }
}
