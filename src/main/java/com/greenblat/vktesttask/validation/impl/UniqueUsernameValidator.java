package com.greenblat.vktesttask.validation.impl;

import com.greenblat.vktesttask.dto.auth.RegisterRequest;
import com.greenblat.vktesttask.repository.UserRepository;
import com.greenblat.vktesttask.validation.UniqueUsername;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, RegisterRequest> {

    private final UserRepository userRepository;

    @Override
    public boolean isValid(RegisterRequest registerRequest, ConstraintValidatorContext constraintValidatorContext) {
        return userRepository.findByUsername(registerRequest.username())
                .isEmpty();
    }
}
