package com.greenblat.vktesttask.service;

import com.greenblat.vktesttask.dto.PostDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final WebClient webClient;

    public List<PostDto> loadPosts() {
        log.info("load posts");
        return webClient.get()
                .uri(String.join("", "/posts"))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<PostDto>>() {
                })
                .block();
    }

    public PostDto loadPostById(Long id) {
        log.info("log posts with id={}", id);
        return webClient.get()
                .uri(String.join("", "/posts/", id.toString()))
                .retrieve()
                .bodyToMono(PostDto.class)
                .block();
    }

    public PostDto savePost(PostDto postDto) {
        return webClient.post()
                .uri(String.join("", "/posts"))
                .bodyValue(postDto)
                .retrieve()
                .bodyToMono(PostDto.class)
                .block();
    }

    public PostDto updatePost(Long id, PostDto postDto) {
        return webClient.put()
                .uri(String.join("", "/posts/", id.toString()))
                .bodyValue(postDto)
                .retrieve()
                .bodyToMono(PostDto.class)
                .block();
    }

    public void delete(Long id) {
        webClient.delete()
                .uri(String.join("", "/posts/", id.toString()));
    }
}
