package com.vernoraTech.service;

import com.vernoraTech.dto.AuthResponse;
import com.vernoraTech.dto.LoginRequest;
import com.vernoraTech.dto.RegisterRequest;
import com.vernoraTech.entity.UserCredentials;
import com.vernoraTech.repository.UserCredentialsRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class AuthService {

    private final UserCredentialsRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthService(UserCredentialsRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    public AuthResponse register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            return new AuthResponse(
                    null,
                    request.getEmail(),
                    false, // emailVerified
                    false, // success
                    "Email already registered. Please use a different email or try logging in."
            );
        }

        try {
            UserCredentials user = new UserCredentials();
            user.setEmail(request.getEmail());
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            user.setEmailVerified(false);
            user.setIsActive(true);

            user = userRepository.save(user);

            String token = null;
            try {
                token = jwtTokenService.generateToken(user.getEmail(), user.getId());
            } catch (Exception ex) {
                ex.printStackTrace(); // optional logging
            }

            return new AuthResponse(
                    token,
                    user.getEmail(),
                    user.getEmailVerified(),
                    true, // success = true because user saved
                    token != null
                            ? "Registration successful! Please verify your email address."
                            : "Registration successful, but token generation failed."
            );

        } catch (Exception e) {
            return new AuthResponse(
                    null,
                    request.getEmail(),
                    false,
                    false, // success = false
                    "Registration failed. Please try again."
            );
        }
    }



    public AuthResponse login(LoginRequest request) {
        Optional<UserCredentials> userOpt = userRepository.findByEmail(request.getEmail());

        if (!userOpt.isPresent()) {
            return new AuthResponse(null, request.getEmail(), false, false, "Invalid email or password.");
        }

        UserCredentials user = userOpt.get();

        if (!user.getIsActive()) {
            return new AuthResponse(null, request.getEmail(), false, false,
                    "Account is deactivated. Please contact support.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return new AuthResponse(null, request.getEmail(), false, false, "Invalid email or password.");
        }

        try {
            userRepository.updateLastLogin(user.getId(), LocalDateTime.now());

            String token = jwtTokenService.generateToken(user.getEmail(), user.getId());

            String message = user.getEmailVerified()
                    ? "Login successful!"
                    : "Login successful! Please verify your email address for full access.";

            return new AuthResponse(token, user.getEmail(), user.getEmailVerified(), true, message);

        } catch (Exception e) {
            return new AuthResponse(null, request.getEmail(), false, false, "Login failed. Please try again.");
        }
    }


    public String verifyEmail(String email) {
        Optional<UserCredentials> userOpt = userRepository.findByEmail(email);

        if (!userOpt.isPresent()) {
            return "Email not found in our records.";
        }

        UserCredentials user = userOpt.get();

        if (Boolean.TRUE.equals(user.getEmailVerified())) {
            return "Email is already verified.";
        }

        userRepository.verifyEmail(email);
        return "Email verified successfully!";
    }



    public Optional<UserCredentials> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
