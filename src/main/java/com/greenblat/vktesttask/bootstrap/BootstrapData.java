package com.greenblat.vktesttask.bootstrap;

import com.greenblat.vktesttask.model.RoleEntity;
import com.greenblat.vktesttask.model.enums.Role;
import com.greenblat.vktesttask.repository.RoleEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BootstrapData implements CommandLineRunner {

    private final RoleEntityRepository repository;

    @Override
    public void run(String... args)  {
        repository.save(
                RoleEntity.builder()
                        .roleName(Role.ALBUMS)
                        .build()
        );
        repository.save(
                RoleEntity.builder()
                        .roleName(Role.USERS)
                        .build()
        );
        repository.save(
                RoleEntity.builder()
                        .roleName(Role.POSTS)
                        .build()
        );
        repository.save(
                RoleEntity.builder()
                        .roleName(Role.ADMIN)
                        .build()
        );
    }


}
