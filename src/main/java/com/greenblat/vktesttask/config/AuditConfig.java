package com.greenblat.vktesttask.config;

import com.greenblat.vktesttask.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class AuditConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        Authentication authentication =  SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || isAnonymousUser(authentication)) {
            return () -> Optional.of("Unknown user");
        }
        User userPrincipal = (User) authentication.getPrincipal();
        return () -> Optional.ofNullable(userPrincipal.getUsername());
    }

    private boolean isAnonymousUser(Authentication authentication) {
        return authentication instanceof AnonymousAuthenticationToken;
    }

}