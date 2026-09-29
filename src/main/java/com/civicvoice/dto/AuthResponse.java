package com.civicvoice.dto;

public record AuthResponse(
        String token,
        String username,
        String role
) {
}
