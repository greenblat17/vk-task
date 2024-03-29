package com.greenblat.vktesttask.config;

import com.greenblat.vktesttask.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static com.greenblat.vktesttask.model.enums.Permission.*;
import static com.greenblat.vktesttask.model.enums.Role.*;
import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    private final LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(registry -> {
                    registry.requestMatchers("/api/v1/auth/**").permitAll();
                    registry.requestMatchers("/api/v1/admin/**").hasRole(ADMIN.name());

                    registry.requestMatchers("/api/v1/posts/**").hasAnyRole(ADMIN.name(), POSTS.name());
                    registry.requestMatchers(GET, "/api/v1/posts/**").hasAnyAuthority(POST_READ.name(), ADMIN_READ.name());
                    registry.requestMatchers(POST, "/api/v1/posts/**").hasAnyAuthority(POST_CREATE.name(), ADMIN_CREATE.name());
                    registry.requestMatchers(PUT, "/api/v1/posts/**").hasAnyAuthority(POST_UPDATE.name(), ADMIN_UPDATE.name());
                    registry.requestMatchers(DELETE, "/api/v1/posts/**").hasAnyAuthority(POST_DELETE.name(), ADMIN_DELETE.name());

                    registry.requestMatchers("/api/v1/users/**").hasAnyRole(ADMIN.name(), USERS.name());
                    registry.requestMatchers(GET, "/api/v1/users/**").hasAnyAuthority(USER_READ.name(), ADMIN_READ.name());
                    registry.requestMatchers(POST, "/api/v1/users/**").hasAnyAuthority(USER_CREATE.name(), ADMIN_CREATE.name());
                    registry.requestMatchers(PUT, "/api/v1/users/**").hasAnyAuthority(USER_UPDATE.name(), ADMIN_UPDATE.name());
                    registry.requestMatchers(DELETE, "/api/v1/users/**").hasAnyAuthority(USER_DELETE.name(), ADMIN_DELETE.name());

                    registry.requestMatchers("/api/v1/albums/**").hasAnyRole(ADMIN.name(), ALBUMS.name());
                    registry.requestMatchers(GET, "/api/v1/albums/**").hasAnyAuthority(ALBUM_READ.name(), ADMIN_READ.name());
                    registry.requestMatchers(POST, "/api/v1/albums/**").hasAnyAuthority(ALBUM_CREATE.name(), ADMIN_CREATE.name());
                    registry.requestMatchers(PUT, "/api/v1/albums/**").hasAnyAuthority(ALBUM_UPDATE.name(), ADMIN_UPDATE.name());
                    registry.requestMatchers(DELETE, "/api/v1/albums/**").hasAnyAuthority(ALBUM_DELETE.name(), ADMIN_DELETE.name());

                    registry.anyRequest().authenticated();
                })
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(configurer -> {
                    configurer.logoutUrl("/api/v1/auth/logout");
                    configurer.addLogoutHandler(logoutHandler);
                    configurer.logoutSuccessHandler((request, response, authentication) ->
                            SecurityContextHolder.clearContext());
                });


        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        var authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
