package com.greenblat.vktesttask.post;

public record PostDto(
        Long id,
        Long userId,
        String title,
        String body
) {
}
