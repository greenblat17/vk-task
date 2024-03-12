package com.greenblat.vktesttask.controller;

import com.greenblat.vktesttask.model.enums.Role;
import com.greenblat.vktesttask.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRoleService userRoleService;

    @PostMapping("/{userId}")
    public void addRole(@RequestParam("role") Role role,
                        @PathVariable Long userId) {
        userRoleService.updateRole(userId, role);
    }

}
