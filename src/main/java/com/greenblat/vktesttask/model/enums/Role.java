package com.greenblat.vktesttask.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.greenblat.vktesttask.model.enums.Permission.*;

@Getter
@RequiredArgsConstructor
public enum Role {

    ADMIN(
            Set.of(ADMIN_UPDATE, ADMIN_CREATE, ADMIN_READ, ADMIN_DELETE)
    ),
    POSTS(
            Set.of(POST_CREATE, POST_DELETE, POST_UPDATE, POST_READ)
    ),
    USERS(
            Set.of(USER_UPDATE, USER_DELETE, USER_READ, USER_CREATE)
    ),
    ALBUMS(
            Set.of(ALBUM_CREATE, ALBUM_DELETE, ALBUM_UPDATE, ALBUM_READ)
    );

    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
