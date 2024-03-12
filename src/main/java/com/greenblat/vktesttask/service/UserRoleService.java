package com.greenblat.vktesttask.service;

import com.greenblat.vktesttask.exception.ResourceNotFoundException;
import com.greenblat.vktesttask.model.User;
import com.greenblat.vktesttask.model.UserRole;
import com.greenblat.vktesttask.model.enums.Role;
import com.greenblat.vktesttask.repository.RoleEntityRepository;
import com.greenblat.vktesttask.repository.UserRepository;
import com.greenblat.vktesttask.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final RoleEntityRepository roleEntityRepository;
    private final UserRepository userRepository;

    public void saveUserRole(User user) {
        var userRole1 = UserRole.builder()
                .role(roleEntityRepository.findByRoleName(Role.USERS).orElseThrow())
                .user(user)
                .build();
        userRoleRepository.save(userRole1);
    }

    public void updateRole(Long userId, Role role) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        var roleEntity = roleEntityRepository.findByRoleName(role)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        var userRole = UserRole.builder()
                .user(user)
                .role(roleEntity)
                .build();
        userRoleRepository.save(userRole);
    }
}
