package com.vernoraTech.controller;

import com.vernoraTech.dto.AuthResponse;
import com.vernoraTech.dto.LoginRequest;
import com.vernoraTech.dto.RegisterRequest;
import com.vernoraTech.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);

            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> result = new HashMap<>();
                result.put("message", response.getMessage());

                // Optional: 409 Conflict if email already exists
                if (response.getMessage().toLowerCase().contains("already registered")) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
                }

                return ResponseEntity.badRequest().body(result);
            }

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            
            if (response.getToken() != null) {
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", response.getMessage());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String email) {
        try {
            String result = authService.verifyEmail(email);

            if ("Email verified successfully!".equals(result)) {
                return ResponseEntity.ok(Map.of("success", true, "message", result));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                     .body(Map.of("success", false, "message", result));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("error", "Email verification failed: " + e.getMessage()));
        }
    }

    
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            // This endpoint would be protected by JWT middleware
            // Implementation would extract user details from token
            Map<String, String> response = new HashMap<>();
            response.put("message", "Profile endpoint - protected by JWT middleware");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get profile: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}