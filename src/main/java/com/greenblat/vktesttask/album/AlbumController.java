package com.greenblat.vktesttask.album;

import com.greenblat.vktesttask.post.PostDto;
import com.greenblat.vktesttask.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;

    @GetMapping
    public ResponseEntity<List<AlbumDto>> getAllAlbums() {
        return ResponseEntity.ok(albumService.loadAlbums());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlbumDto> getAlbum(@PathVariable("id") Long albumId) {
        return ResponseEntity.ok(albumService.loadAlbumById(albumId));
    }

    @PostMapping
    public ResponseEntity<AlbumDto> saveAlbum(@RequestBody AlbumDto albumDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(albumService.saveAlbum(albumDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlbumDto> updateAlbum(@PathVariable("id") Long albumId,
                                              @RequestBody AlbumDto albumDto) {
        return ResponseEntity.ok(albumService.updateAlbum(albumId, albumDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable("id") Long albumId) {
        albumService.delete(albumId);
        return ResponseEntity
                .noContent()
                .build();
    }

}
