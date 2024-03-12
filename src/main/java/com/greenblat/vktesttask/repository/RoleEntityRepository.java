package com.greenblat.vktesttask.repository;

import com.greenblat.vktesttask.model.RoleEntity;
import com.greenblat.vktesttask.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleEntityRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByRoleName(Role roleName);
}
