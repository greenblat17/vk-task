package com.greenblat.vktesttask.album;

import com.greenblat.vktesttask.post.PostDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlbumService {

    private final WebClient webClient;

    public List<AlbumDto> loadAlbums() {
        log.info("load posts");
        return webClient.get()
                .uri(String.join("", "/albums"))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<AlbumDto>>() {
                })
                .block();
    }

    public AlbumDto loadAlbumById(Long id) {
        log.info("log posts with id={}", id);
        return webClient.get()
                .uri(String.join("", "/albums/", id.toString()))
                .retrieve()
                .bodyToMono(AlbumDto.class)
                .block();
    }

    public AlbumDto saveAlbum(AlbumDto albumDto) {
        return webClient.post()
                .uri(String.join("", "/albums"))
                .bodyValue(albumDto)
                .retrieve()
                .bodyToMono(AlbumDto.class)
                .block();
    }

    public AlbumDto updateAlbum(Long id, AlbumDto albumDto) {
        return webClient.put()
                .uri(String.join("", "/albums/", id.toString()))
                .bodyValue(albumDto)
                .retrieve()
                .bodyToMono(AlbumDto.class)
                .block();
    }

    public void delete(Long id) {
        webClient.delete()
                .uri(String.join("", "/albums/", id.toString()));
    }
}
