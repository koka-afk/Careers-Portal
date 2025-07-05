package com.career.portal.Config;

import com.career.portal.services.JwtUtil;
//import com.career.portal.services.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

//    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");
        log.debug("Processing request to: {}", request.getRequestURI());
        log.debug("Authorization header: {}", authorizationHeader);


        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            log.debug("Extracted JWT: {}", jwt.substring(0, Math.min(jwt.length(), 20)) + "...");
            try {
                username = jwtUtil.extractUsername(jwt);
                log.debug("Extracted username: {}", username);
            } catch (IllegalArgumentException e) {
                log.error("Unable to get JWT Token", e);
            } catch (ExpiredJwtException e) {
                log.error("JWT Token Expired", e);
            }
        }


        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            log.debug("Loading user details for username: {}", username);
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                log.debug("User details loaded: {}", userDetails.getUsername());
                log.debug("User authorities: {}", userDetails.getAuthorities());
                try {
                    boolean isValid = jwtUtil.validateToken(jwt, userDetails);
                    log.debug("JWT validation result: {}", isValid);
                    if (isValid) {
                        log.debug("JWT token is valid, setting authentication");

                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);

                        log.debug("Authentication set in SecurityContext");
                    } else {
                        log.warn("JWT token validation failed for user: {}", username);
                    }
                } catch (Exception e) {
                    log.error("Exception during JWT validation for user: {}", username, e);
                }
            }catch (Exception e) {
                log.error("Error loading user details for username: {}", username, e);
            }
        }
        chain.doFilter(request, response);
    }
}
