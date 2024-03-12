package com.greenblat.vktesttask.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Permission {

    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_CREATE("admin:create"),
    ADMIN_DELETE("admin:delete"),

    POST_READ("post:read"),
    POST_UPDATE("post:update"),
    POST_CREATE("post:create"),
    POST_DELETE("post:delete"),

    ALBUM_READ("album:read"),
    ALBUM_UPDATE("album:update"),
    ALBUM_CREATE("album:create"),
    ALBUM_DELETE("album:delete"),


    USER_READ("user:read"),
    USER_UPDATE("user:update"),
    USER_CREATE("user:create"),
    USER_DELETE("user:delete");

    private final String permission;
}
