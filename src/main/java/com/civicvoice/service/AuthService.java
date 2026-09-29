package com.civicvoice.service;

import com.civicvoice.dto.AuthResponse;
import com.civicvoice.dto.LoginRequest;
import com.civicvoice.dto.RegisterRequest;
import com.civicvoice.entity.Role;
import com.civicvoice.entity.UserAccount;
import com.civicvoice.exception.ApiException;
import com.civicvoice.repository.UserRepository;
import com.civicvoice.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        if (request == null) {
            throw new ApiException("Registration request is required");
        }

        String username = request.username() == null ? "" : request.username().trim();
        if (username.isBlank()) {
            throw new ApiException("Username is required");
        }

        if (request.password() == null || request.password().isBlank()) {
            throw new ApiException("Password is required");
        }

        if (userRepository.existsByUsername(username)) {
            throw new ApiException("User already exists");
        }

        Role role = request.role() == null || request.role().isBlank() ? Role.USER : Role.valueOf(request.role().trim().toUpperCase());

        UserAccount user = UserAccount.builder()
                .username(username)
                .password(passwordEncoder.encode(request.password()))
                .roles(Set.of(role))
                .build();

        userRepository.save(user);

        return login(new LoginRequest(username, request.password()));
    }

    public AuthResponse login(LoginRequest request) {
        if (request == null) {
            throw new ApiException("Login request is required");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);

        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                .orElse("USER");

        return new AuthResponse(token, userDetails.getUsername(), role);
    }
}
