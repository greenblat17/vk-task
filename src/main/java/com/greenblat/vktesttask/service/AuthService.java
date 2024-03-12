package com.greenblat.vktesttask.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenblat.vktesttask.dto.auth.AuthRequest;
import com.greenblat.vktesttask.dto.auth.AuthResponse;
import com.greenblat.vktesttask.dto.auth.RegisterRequest;
import com.greenblat.vktesttask.exception.ObjectResponseException;
import com.greenblat.vktesttask.exception.ResourceNotFoundException;
import com.greenblat.vktesttask.mapper.UserMapper;
import com.greenblat.vktesttask.model.Token;
import com.greenblat.vktesttask.model.User;
import com.greenblat.vktesttask.model.enums.TokenType;
import com.greenblat.vktesttask.repository.TokenRepository;
import com.greenblat.vktesttask.repository.UserRepository;
import com.greenblat.vktesttask.security.JwtService;
import com.greenblat.vktesttask.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRoleService userRoleService;

    public AuthResponse registerUser(RegisterRequest request) {
        User user = userMapper.mapToUser(request, passwordEncoder.encode(request.password()));
        var savedUser = userRepository.save(user);
        userRoleService.saveUserRole(savedUser);

        var userDetails = convertToUserDetails(user);
        var jwtToken = jwtService.generateToken(userDetails);
        var refreshToken = jwtService.generateRefreshToken(userDetails);

        saveUserToken(savedUser, jwtToken);

        return new AuthResponse(jwtToken, refreshToken);
    }

    public AuthResponse authenticateUser(AuthRequest request) {
        var authInputToken = new UsernamePasswordAuthenticationToken(
                request.username(),
                request.password()
        );
        authenticationManager.authenticate(authInputToken);
        var user = getUser(request.username());

        var userDetails = convertToUserDetails(user);
        var jwtToken = jwtService.generateRefreshToken(userDetails);
        var refreshToken = jwtService.generateRefreshToken(userDetails);

        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);

        return new AuthResponse(jwtToken, refreshToken);
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        refreshToken = authHeader.substring(7);
        username = jwtService.extractUsername(refreshToken);

        if (username != null) {
            var user = getUser(username);
            var userDetails = convertToUserDetails(user);
            if (jwtService.isRefreshTokenValid(refreshToken, userDetails)) {
                var accessToken = jwtService.generateToken(userDetails);

                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);

                var authResponse = new AuthResponse(accessToken, refreshToken);
                writeToResponse(response, authResponse);
            }
        }
    }

    private void writeToResponse(HttpServletResponse response, AuthResponse authResponse) {
        try {
            new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
        } catch (IOException e) {
            throw new ObjectResponseException(e.getMessage());
        }
    }

    private UserDetails convertToUserDetails(User user) {
        return new UserDetailsImpl(user);
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User with username [%s] not found"
                                        .formatted(username)
                        ));
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
