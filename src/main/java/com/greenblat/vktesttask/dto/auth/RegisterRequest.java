package com.greenblat.vktesttask.dto.auth;

public record RegisterRequest(String username,
                              String email,
                              String password) {
}
