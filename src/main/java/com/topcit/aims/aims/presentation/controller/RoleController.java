package com.topcit.aims.aims.presentation.controller;

import com.topcit.aims.aims.domain.repository.PermissionRepository;
import com.topcit.aims.aims.domain.repository.RoleRepository;
import com.topcit.aims.aims.domain.repository.UserRepository;
import com.topcit.aims.aims.infrastructure.persistence.entity.Permission;
import com.topcit.aims.aims.infrastructure.persistence.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchTransactionManager;
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
    @Autowired
    private UserRepository userRepository; //// Inject this to check constraints before deleting
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles(){
        return ResponseEntity.ok(roleRepository.findAll());
    }
    // Them role moi
    @PostMapping
    public ResponseEntity<?> createRole(@RequestBody Role request){
        if (roleRepository.findByName(request.getName()).isPresent()){
            return ResponseEntity.badRequest().body(Map.of("message", "Role đã tồn tại!"));
        }
        Role newRole = new Role();
        newRole.setName(request.getName());
        newRole.setDisplayName(request.getDisplayName());
        return ResponseEntity.ok(roleRepository.save(newRole));
    }
    // Cap nhat Role
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRole(@PathVariable Long id, @RequestBody Role request){
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Role!"));
        existingRole.setName(request.getName());
        existingRole.setDisplayName(request.getDisplayName());
        return ResponseEntity.ok(roleRepository.save(existingRole));
    }
    // Xoa Role
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable Long id){
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tìm thấy Role!"));
        boolean isRoleInUse = userRepository.existsByRoleId(id);
        if (isRoleInUse){
            return ResponseEntity.badRequest().body(Map.of("message", "Không thể xóa! Đang có người dùng thuộc nhóm quyền này."));
        }
        // Xóa liên kết trong bảng role_permissions trước
        role.getPermissions().clear();
        roleRepository.save(role);
        // Sau đó mới xóa Role
        roleRepository.delete(role);
        return ResponseEntity.ok(Map.of("message", "Xóa Role thành công!"));
    }
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
