package com.career.portal.controllers;

import com.career.portal.dto.AuthenticationRequest;
import com.career.portal.dto.AuthenticationResponse;
import com.career.portal.dto.PasswordReset;
import com.career.portal.dto.PasswordResetRequest;
import com.career.portal.models.User;
import com.career.portal.services.JwtUtil;
//import com.career.portal.services.UserDetailsServiceImpl;
import com.career.portal.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword())
            );
            log.info("Authentication Success: {}", authentication);
            log.info("Authenticating user {}", authentication.getPrincipal());
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
        }  catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Please verify your email address before logging in.");
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Invalid email or password.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred. Please try again later.");
        }
    }

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token){
        try{
            userService.verifyEmail(token);
            return ResponseEntity.ok("Email verified successfully");
        }catch (IllegalArgumentException | IllegalStateException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend-verification-email")
    public ResponseEntity<String> resendVerificationEmail(@RequestBody Map<String, String> request) {
        try {
            userService.resendVerificationEmail(request.get("email"));
            return ResponseEntity.ok("A new verification email has been sent. Please check your inbox.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<String> requestPasswordReset(@RequestBody PasswordResetRequest request){
        try{
            userService.requestPasswordReset(request.getEmail());
            return ResponseEntity.ok("Password reset email sent");
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordReset request){
        try{
            userService.resetPassword(request.getToken(), request.getPassword());
            return ResponseEntity.ok("Password reset successful");
        }catch (IllegalArgumentException | IllegalStateException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
