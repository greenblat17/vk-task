package com.greenblat.vktesttask.controller;

import com.greenblat.vktesttask.audit.Audit;
import com.greenblat.vktesttask.dto.PostDto;
import com.greenblat.vktesttask.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    @Audit
    public ResponseEntity<List<PostDto>> getAllPosts() {
        return ResponseEntity.ok(postService.loadPosts());
    }

    @GetMapping("/{id}")
    @Audit
    public ResponseEntity<PostDto> getPost(@PathVariable("id") Long postId) {
        return ResponseEntity.ok(postService.loadPostById(postId));
    }

    @PostMapping
    @Audit
    public ResponseEntity<PostDto> savePost(@RequestBody PostDto postDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(postService.savePost(postDto));
    }

    @PutMapping("/{id}")
    @Audit
    public ResponseEntity<PostDto> updatePost(@PathVariable("id") Long postId,
                                              @RequestBody PostDto postDto) {
        return ResponseEntity.ok(postService.updatePost(postId, postDto));
    }

    @DeleteMapping("/{id}")
    @Audit
    public ResponseEntity<Void> deletePost(@PathVariable("id") Long postId) {
        postService.delete(postId);
        return ResponseEntity
                .noContent()
                .build();
    }

}
