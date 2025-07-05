package com.career.portal.controllers;

import com.career.portal.dto.AuthenticationRequest;
import com.career.portal.dto.AuthenticationResponse;
import com.career.portal.models.User;
import com.career.portal.services.JwtUtil;
//import com.career.portal.services.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword())
        );
        User userDetails = (User) authentication.getPrincipal();
        final String jwt = jwtUtil.generateToken(userDetails);
        AuthenticationResponse response = AuthenticationResponse.builder()
                .jwt(jwt)
                .id(userDetails.getId())
                .email(userDetails.getEmail())
                .firstName(userDetails.getFirstName())
                .lastName(userDetails.getLastName())
                .role(userDetails.getRole())
                .phone(userDetails.getPhone())
                .resumePath(userDetails.getResumePath())
                .createdAt(userDetails.getCreatedAt())
                .build();
        return ResponseEntity.ok(response);
    }



}
