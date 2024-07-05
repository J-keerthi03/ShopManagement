package com.example.logic.controller;

import com.example.logic.entity.User;
import com.example.logic.service.UserDetailsServiceImpl;
import com.example.logic.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<?> login(@RequestBody User loginUser) {
        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword()));
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginUser.getUsername());
            String token = "";

            if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                // Generate JWT token for admin
                token = jwtUtil.generateToken(userDetails);
            } else if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"))) {
                // Generate Basic token for user
                token = generateBasicToken(userDetails);
            } else {
                // Handle other roles or scenarios
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
            }

            // Return the token as a response
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (UsernameNotFoundException e) {
            // Handle exception if user is not found
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } catch (Exception e) {
            // Handle generic authentication exception
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: " + e.getMessage());
        }
    }

    // Generate Basic token for user role
    private String generateBasicToken(UserDetails userDetails) {
        String usernameAndPassword = userDetails.getUsername() + ":" + userDetails.getPassword();
        byte[] encodedBytes = java.util.Base64.getEncoder().encode(usernameAndPassword.getBytes());
        return "Basic " + new String(encodedBytes);
    }

    // Inner class to encapsulate JWT response
    class JwtResponse {
        private String token;

        public JwtResponse(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
