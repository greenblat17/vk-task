package com.greenblat.vktesttask.security;

import com.greenblat.vktesttask.model.RoleEntity;
import com.greenblat.vktesttask.model.User;
import com.greenblat.vktesttask.model.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public record UserDetailsImpl(
        User user
) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getUserRoleList().stream()
                .map(UserRole::getRole)
                .map(RoleEntity::getRoleName)
                .flatMap(role -> role.getAuthorities().stream())
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
