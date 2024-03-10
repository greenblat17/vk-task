package com.greenblat.vktesttask.post;

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
    public ResponseEntity<List<PostDto>> getAllPosts() {
        return ResponseEntity.ok(postService.loadPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPost(@PathVariable("id") Long postId) {
        return ResponseEntity.ok(postService.loadPostById(postId));
    }

    @PostMapping
    public ResponseEntity<PostDto> savePost(@RequestBody PostDto postDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(postService.savePost(postDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDto> updatePost(@PathVariable("id") Long postId,
                                              @RequestBody PostDto postDto) {
        return ResponseEntity.ok(postService.updatePost(postId, postDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable("id") Long postId) {
        postService.delete(postId);
        return ResponseEntity
                .noContent()
                .build();
    }

}
