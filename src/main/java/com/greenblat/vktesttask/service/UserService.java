package com.greenblat.vktesttask.service;

import com.greenblat.vktesttask.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final WebClient webClient;

    public List<UserDto> loadUsers() {
        log.info("load users");
        return webClient.get()
                .uri(String.join("", "/users"))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<UserDto>>() {
                })
                .block();
    }

    public UserDto loadUserById(Long id) {
        log.info("log users with id={}", id);
        return webClient.get()
                .uri(String.join("", "/users/", id.toString()))
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();
    }

    public UserDto saveUser(UserDto userDto) {
        return webClient.post()
                .uri(String.join("", "/users"))
                .bodyValue(userDto)
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();
    }

    public UserDto updatePost(Long id, UserDto userDto) {
        return webClient.put()
                .uri(String.join("", "/users/", id.toString()))
                .bodyValue(userDto)
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();
    }

    public void delete(Long id) {
        webClient.delete()
                .uri(String.join("", "/users/", id.toString()));
    }
}
