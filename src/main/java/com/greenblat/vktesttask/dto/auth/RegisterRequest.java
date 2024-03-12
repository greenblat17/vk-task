package com.greenblat.vktesttask.dto.auth;

import com.greenblat.vktesttask.validation.UniqueUsername;
import jakarta.validation.constraints.NotBlank;

@UniqueUsername
public record RegisterRequest(@NotBlank String username,
                              @NotBlank String password) {
}
