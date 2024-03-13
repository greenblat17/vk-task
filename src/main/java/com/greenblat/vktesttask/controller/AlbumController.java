package com.greenblat.vktesttask.controller;

import com.greenblat.vktesttask.audit.Audit;
import com.greenblat.vktesttask.dto.AlbumDto;
import com.greenblat.vktesttask.service.AlbumService;
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
    @Audit
    public ResponseEntity<List<AlbumDto>> getAllAlbums() {
        return ResponseEntity.ok(albumService.loadAlbums());
    }

    @GetMapping("/{id}")
    @Audit
    public ResponseEntity<AlbumDto> getAlbum(@PathVariable("id") Long albumId) {
        return ResponseEntity.ok(albumService.loadAlbumById(albumId));
    }

    @PostMapping
    @Audit
    public ResponseEntity<AlbumDto> saveAlbum(@RequestBody AlbumDto albumDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(albumService.saveAlbum(albumDto));
    }

    @PutMapping("/{id}")
    @Audit
    public ResponseEntity<AlbumDto> updateAlbum(@PathVariable("id") Long albumId,
                                              @RequestBody AlbumDto albumDto) {
        return ResponseEntity.ok(albumService.updateAlbum(albumId, albumDto));
    }

    @DeleteMapping("/{id}")
    @Audit
    public ResponseEntity<Void> deleteAlbum(@PathVariable("id") Long albumId) {
        albumService.delete(albumId);
        return ResponseEntity
                .noContent()
                .build();
    }

}
