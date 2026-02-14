package com.topcit.aims.aims.presentation.controller;

import com.topcit.aims.aims.domain.repository.PermissionRepository;
import com.topcit.aims.aims.domain.repository.RoleRepository;
import com.topcit.aims.aims.infrastructure.persistence.entity.Permission;
import com.topcit.aims.aims.infrastructure.persistence.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/roles")
public class RoleController {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PermissionRepository permissionRepository;
    //1. lay du lieu de ve bang Matrix
    @GetMapping("/permission-matrix")
    public ResponseEntity<?> getPermissionMatrix(){
        List<Role> roles = roleRepository.findAll();
        List<Permission> permissions = permissionRepository.findAll();
        List<Role> roless = roleRepository.findAllWithPermissions();
        Map<String, List<Permission>> groupedPermissions = permissions.stream()
                .collect(Collectors.groupingBy(Permission::getModule));
        Map<String, Object> response = new HashMap<>();
        response.put("roles", roles);
        response.put("groupedPermissions", groupedPermissions);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/{roleId}/permissions")
    public ResponseEntity<?> updatePermissions(@PathVariable Long roleId, @RequestBody List<Long> permissionIds){
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        List<Permission> newPermissions = permissionRepository.findAllById(permissionIds);
        role.setPermissions(new HashSet<>(newPermissions));
        roleRepository.save(role);
        return ResponseEntity.ok(Map.of("message", "Cập nhật quyền thành công!"));
    }
}
