package com.innocito.standupai.controller;

import com.innocito.standupai.dto.AuthRequest;
import com.innocito.standupai.dto.AuthResponse;
import com.innocito.standupai.dto.LoginRequest;
import com.innocito.standupai.dto.ResetPassword;
import com.innocito.standupai.entity.User;
import com.innocito.standupai.entity.UserRole;
import com.innocito.standupai.repository.UserRepository;
import com.innocito.standupai.security.DomainValidator;
import com.innocito.standupai.security.JwtUtil;
import com.innocito.standupai.service.PasswordResetService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Data
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetService passwordResetService;
    private final DomainValidator domainValidator;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid credentials");
        }

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetails userDetails = user; // User implements UserDetails
        String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(jwt, user.getUsername(), user.getEmail(), user.getId()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest authRequest) {
        String email = authRequest.getUsername();
        if (!domainValidator.isDomainAllowed(email)) {
            return ResponseEntity.badRequest().body("Email domain is not allowed.");
        }

        if (userRepository.existsByUsername(email)) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        User user = new User();
        user.setUsername(authRequest.getUsername());
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
        user.setEmail(authRequest.getUsername());
        user.setFullName(authRequest.getFullName());

        // ✅ Use the role from request if valid
        try {
            user.setRole(UserRole.valueOf(authRequest.getRole().toUpperCase()));
        } catch (IllegalArgumentException | NullPointerException e) {
            user.setRole(UserRole.USER); // fallback default
        }

        User savedUser = userRepository.save(user);

        String jwt = jwtUtil.generateToken(savedUser); // user implements UserDetails
        return ResponseEntity.ok(new AuthResponse(jwt, savedUser.getUsername(), savedUser.getEmail(), savedUser.getId()));
    }


    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPassword request) {
        try {
            String result = passwordResetService.resetPassword(request);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
