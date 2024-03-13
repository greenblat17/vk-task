package com.greenblat.vktesttask.controller;

import com.greenblat.vktesttask.audit.Audit;
import com.greenblat.vktesttask.dto.UserDto;
import com.greenblat.vktesttask.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @Audit
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.loadUsers());
    }

    @GetMapping("/{id}")
    @Audit
    public ResponseEntity<UserDto> getUser(@PathVariable("id") Long userId) {
        return ResponseEntity.ok(userService.loadUserById(userId));
    }

    @PostMapping
    @Audit
    public ResponseEntity<UserDto> saveUser(@RequestBody UserDto userDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.saveUser(userDto));
    }

    @PutMapping("/{id}")
    @Audit
    public ResponseEntity<UserDto> updateUser(@PathVariable("id") Long userId,
                                              @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.updatePost(userId, userDto));
    }

    @DeleteMapping("/{id}")
    @Audit
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long userId) {
        userService.delete(userId);
        return ResponseEntity
                .noContent()
                .build();
    }
}
