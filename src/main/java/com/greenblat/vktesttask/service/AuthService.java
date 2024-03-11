package com.greenblat.vktesttask.service;

import com.greenblat.vktesttask.dto.auth.AuthRequest;
import com.greenblat.vktesttask.dto.auth.AuthResponse;
import com.greenblat.vktesttask.dto.auth.RegisterRequest;
import com.greenblat.vktesttask.exception.ResourceNotFoundException;
import com.greenblat.vktesttask.mapper.UserMapper;
import com.greenblat.vktesttask.model.User;
import com.greenblat.vktesttask.repository.UserRepository;
import com.greenblat.vktesttask.security.JwtService;
import com.greenblat.vktesttask.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse registerUser(RegisterRequest request) {
        User user = userMapper.mapToUser(request, passwordEncoder.encode(request.password()));
        userRepository.save(user);
        return buildAuthResponseWithToken(user);
    }

    public AuthResponse authenticateUser(AuthRequest request) {
        var authInputToken = new UsernamePasswordAuthenticationToken(
                request.username(),
                request.password()
        );
        authenticationManager.authenticate(authInputToken);
        var user = userRepository.findByUsername(request.username())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User with username [%s] not found"
                                        .formatted(request.username())
                        ));
        return buildAuthResponseWithToken(user);
    }

    private AuthResponse buildAuthResponseWithToken(User user) {
        var userDetails = new UserDetailsImpl(user);
        var jwtToken = jwtService.generateToken(userDetails);
        return new AuthResponse(jwtToken);
    }
}
