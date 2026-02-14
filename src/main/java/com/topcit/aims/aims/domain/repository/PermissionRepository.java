package com.topcit.aims.aims.domain.repository;

import com.topcit.aims.aims.infrastructure.persistence.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
}
