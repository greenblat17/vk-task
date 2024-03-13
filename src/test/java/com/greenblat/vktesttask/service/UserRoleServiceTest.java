package com.greenblat.vktesttask.service;

import com.greenblat.vktesttask.exception.ResourceNotFoundException;
import com.greenblat.vktesttask.model.RoleEntity;
import com.greenblat.vktesttask.model.User;
import com.greenblat.vktesttask.model.UserRole;
import com.greenblat.vktesttask.model.enums.Role;
import com.greenblat.vktesttask.repository.RoleEntityRepository;
import com.greenblat.vktesttask.repository.UserRepository;
import com.greenblat.vktesttask.repository.UserRoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRoleServiceTest {
    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private RoleEntityRepository roleEntityRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserRoleService userRoleService;

    @Test
    public void testSaveUserRole() {
        User user = new User();
        Role role = Role.USERS;
        RoleEntity roleEntity = RoleEntity.builder()
                .roleName(role)
                .build();
        when(roleEntityRepository.findByRoleName(role)).thenReturn(java.util.Optional.of(roleEntity));

        userRoleService.saveUserRole(user);

        verify(roleEntityRepository).findByRoleName(role);
        verify(userRoleRepository).save(any(UserRole.class));
    }

    @Test
    public void testUpdateRole() {
        Long userId = 1L;
        Role role = Role.USERS;
        User user = new User();
        RoleEntity roleEntity = RoleEntity.builder()
                .roleName(role)
                .build();
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(roleEntityRepository.findByRoleName(role)).thenReturn(java.util.Optional.of(roleEntity));

        userRoleService.updateRole(userId, role);

        verify(userRepository).findById(userId);
        verify(roleEntityRepository).findByRoleName(role);
        verify(userRoleRepository).save(any(UserRole.class));
    }
    @Test
    public void testSaveUserRoleIfRoleNotFound() {
        User user = new User();
        when(roleEntityRepository.findByRoleName(Role.USERS)).thenReturn(java.util.Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userRoleService.saveUserRole(user));

        verify(roleEntityRepository).findByRoleName(Role.USERS);
        verifyNoMoreInteractions(userRoleRepository);
    }

    @Test
    public void testUpdateRoleIfUserNotFound() {
        Long userId = 1L;
        Role role = Role.USERS;
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userRoleService.updateRole(userId, role));

        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(roleEntityRepository, userRoleRepository);
    }

    @Test
    public void testUpdateRoleIfRoleNotFound() {
        Long userId = 1L;
        Role role = Role.USERS;
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(roleEntityRepository.findByRoleName(role)).thenReturn(java.util.Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userRoleService.updateRole(userId, role));

        verify(userRepository).findById(userId);
        verify(roleEntityRepository).findByRoleName(role);
        verifyNoMoreInteractions(userRoleRepository);
    }

}