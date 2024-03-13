package com.greenblat.vktesttask.service;

import com.greenblat.vktesttask.dto.auth.AuthRequest;
import com.greenblat.vktesttask.dto.auth.AuthResponse;
import com.greenblat.vktesttask.dto.auth.RegisterRequest;
import com.greenblat.vktesttask.exception.ResourceNotFoundException;
import com.greenblat.vktesttask.mapper.UserMapper;
import com.greenblat.vktesttask.model.Token;
import com.greenblat.vktesttask.model.User;
import com.greenblat.vktesttask.repository.TokenRepository;
import com.greenblat.vktesttask.repository.UserRepository;
import com.greenblat.vktesttask.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRoleService userRoleService;

    @InjectMocks
    private AuthService authService;

    @Test
    public void testRegisterUser() {
        RegisterRequest registerRequest = new RegisterRequest("testUser", "testPassword");
        User user = new User();
        String encodedPassword = "encodedPassword";

        when(userMapper.mapToUser(registerRequest, encodedPassword)).thenReturn(user);
        when(passwordEncoder.encode(registerRequest.password())).thenReturn(encodedPassword);
        when(userRepository.save(user)).thenReturn(user);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("testJwtToken");
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn("testRefreshToken");

        AuthResponse authResponse = authService.registerUser(registerRequest);

        verify(userMapper).mapToUser(registerRequest, encodedPassword);
        verify(userRepository).save(user);
        verify(userRoleService).saveUserRole(user);
        verify(jwtService).generateRefreshToken(any(UserDetails.class));
        verify(jwtService).generateToken(any(UserDetails.class));
        assertNotNull(authResponse.accessToken());
        assertNotNull(authResponse.refreshToken());
    }

    @Test
    public void testAuthenticateUser() {
        // Mock data
        AuthRequest authRequest = new AuthRequest("testUser", "testPassword");
        User user = new User();

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("testJwtToken");
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn("testRefreshToken");

        // Perform the test
        AuthResponse authResponse = authService.authenticateUser(authRequest);

        // Verify the interactions
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByUsername("testUser");
        verify(jwtService).generateRefreshToken(any(UserDetails.class));
        verify(jwtService).generateToken(any(UserDetails.class));
        verify(tokenRepository).save(any(Token.class));
        assertNotNull(authResponse.accessToken());
        assertNotNull(authResponse.refreshToken());
    }

    @Test
    public void testAuthenticateUserFailed() {
        AuthRequest authRequest = new AuthRequest("testUser", "testPassword");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                authService.authenticateUser(authRequest));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByUsername("testUser");
    }


    @Test
    public void testRefreshTokenIfUserNotFound() throws Exception {
        // Mock data
        String token = "invalidToken";

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        // Perform the test
        assertThrows(ResourceNotFoundException.class, () ->
                authService.refreshToken(request, response));


        // Verify the interactions
        verify(jwtService).extractUsername(token);
        verify(userRepository).findByUsername("testUser");
        verifyNoMoreInteractions(jwtService, userRepository, tokenRepository);
    }

    @Test
    public void testRefreshTokenIfInvalidHeader()  {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        authService.refreshToken(request, response);

        verifyNoMoreInteractions(jwtService, userRepository, tokenRepository);
    }

}