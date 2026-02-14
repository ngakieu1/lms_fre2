package com.topcit.aims.aims.dto;

import com.topcit.aims.aims.infrastructure.persistence.entity.Permission;
import com.topcit.aims.aims.infrastructure.persistence.entity.Role;

import java.util.List;
import java.util.Map;

public class PermissionMatrixDTO {
    private List<Role> roles;
    private Map<String, List<Permission>> groupedPermissions;
    private List<Long> activePermissionIds;

}
