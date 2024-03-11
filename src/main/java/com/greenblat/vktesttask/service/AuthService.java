package com.greenblat.vktesttask.service;

import com.greenblat.vktesttask.dto.auth.AuthRequest;
import com.greenblat.vktesttask.dto.auth.AuthResponse;
import com.greenblat.vktesttask.dto.auth.RegisterRequest;
import com.greenblat.vktesttask.exception.ResourceNotFoundException;
import com.greenblat.vktesttask.mapper.UserMapper;
import com.greenblat.vktesttask.model.Token;
import com.greenblat.vktesttask.model.TokenType;
import com.greenblat.vktesttask.model.User;
import com.greenblat.vktesttask.repository.TokenRepository;
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

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse registerUser(RegisterRequest request) {
        User user = userMapper.mapToUser(request, passwordEncoder.encode(request.password()));

        var savedUser = userRepository.save(user);
        var authResponse = buildAuthResponseWithToken(savedUser);

        saveUserToken(savedUser, authResponse.token());

        return authResponse;
    }

    public AuthResponse authenticateUser(AuthRequest request) {
        var authInputToken = new UsernamePasswordAuthenticationToken(
                request.username(),
                request.password()
        );
        authenticationManager.authenticate(authInputToken);

        var user = getUser(request.username());
        var authResponse = buildAuthResponseWithToken(user);

        revokeAllUserTokens(user);
        saveUserToken(user, authResponse.token());

        return authResponse;
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User with username [%s] not found"
                                        .formatted(username)
                        ));
    }

    private AuthResponse buildAuthResponseWithToken(User user) {
        var userDetails = new UserDetailsImpl(user);
        var jwtToken = jwtService.generateToken(userDetails);
        return new AuthResponse(jwtToken);
    }

    private void revokeAllUserTokens(User user) {
        var validUserToken = tokenRepository.findAllValidTokensByUser(user.getId());
        if (validUserToken.isEmpty()) {
            return;
        }
        validUserToken.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserToken);
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }
}
