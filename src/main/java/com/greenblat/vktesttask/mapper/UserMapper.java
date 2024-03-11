package com.greenblat.vktesttask.mapper;

import com.greenblat.vktesttask.dto.auth.RegisterRequest;
import com.greenblat.vktesttask.model.User;
import lombok.RequiredArgsConstructor;

@Mapper
@RequiredArgsConstructor
public class UserMapper {

    public User mapToUser(RegisterRequest request, String encodePassword) {
        return User.builder()
                .password(encodePassword)
                .username(request.username())
                .build();
    }

}
