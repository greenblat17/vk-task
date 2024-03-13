package com.greenblat.vktesttask.repository;

import com.greenblat.vktesttask.model.AuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditEntityRepository extends JpaRepository<AuditEntity, Long> {
}
