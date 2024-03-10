package com.greenblat.vktesttask.dto;

public record PostDto(
        Long id,
        Long userId,
        String title,
        String body
) {
}
