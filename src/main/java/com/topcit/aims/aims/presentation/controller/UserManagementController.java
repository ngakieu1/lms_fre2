package com.topcit.aims.aims.presentation.controller;

import com.topcit.aims.aims.domain.repository.RoleRepository;
import com.topcit.aims.aims.domain.repository.UserRepository;
import com.topcit.aims.aims.infrastructure.persistence.entity.Role;
import com.topcit.aims.aims.infrastructure.persistence.entity.UserJpaEntity;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class UserManagementController {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    //1. Lay danh sach user (co ho tro tim kiem)
    @GetMapping
    public ResponseEntity<List<UserJpaEntity>> getAllUsers(@RequestParam(required = false) String search){
        List<UserJpaEntity> users;
        if (search != null && !search.trim().isEmpty()){
            users = userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(search, search);
        } else {
            users = userRepository.findAll();
        }
        users.forEach(u -> u.setPassword(null));
        return ResponseEntity.ok(users);
    }
    //2. Them moi user
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody Map<String, String> request){
        if (userRepository.findByUsername(request.get("username")).isPresent()){
            return ResponseEntity.badRequest().body(Map.of("message", "Tài khoản đã tồn tại!"));
        }
        Role role = roleRepository.findById(Long.parseLong(request.get("roleId")))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Role!"));
        UserJpaEntity newUser = new UserJpaEntity();
        newUser.setUsername(request.get("username"));
        newUser.setEmail(request.get("email"));
        newUser.setFullName(request.get("fullName"));
        newUser.setPhoneNumber(request.get("phoneNumber"));
        newUser.setPassword(passwordEncoder.encode(request.get("password")));
        newUser.setRole(role);
        userRepository.save(newUser);
        return ResponseEntity.ok(Map.of("message", "Tạo tài khoản thành công!"));
    }
    //3. Cap nhat user
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Map<String, String> request){
        UserJpaEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User!"));
        System.out.println("UPDATE USER ID: " + id);
        Role role = roleRepository.findById(Long.parseLong(request.get("roleId")))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Role!"));
        user.setFullName(request.get("fullName"));
        user.setEmail(request.get("email"));
        user.setPhoneNumber(request.get("phoneNumber"));
        user.setRole(role);
        String newPassword = request.get("password");
        if (newPassword != null && !newPassword.trim().isEmpty()){
            user.setPassword(passwordEncoder.encode(newPassword));
        }
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Cập nhật tài khoản thành công!"));
    }
    //4. Xoa user
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id){
        userRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Xóa tài khoản thành công!"));
    }
    //5. Xuat danh sach
    @GetMapping("/export")
    public void exportUsersToCSV(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"users_export.csv\"");

        OutputStream os = response.getOutputStream();
        // Ghi BOM để Excel nhận diện được Tiếng Việt UTF-8
        os.write(new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF });

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
        // Tiêu đề cột
        writer.write("ID,Tài khoản,Họ và tên,Số điện thoại,Email,Nhóm quyền");

        List<UserJpaEntity> users = userRepository.findAll();
        for (UserJpaEntity u : users) {
            String roleName = u.getRole() != null ? u.getRole().getDisplayName() : "N/A";
            // Ghi từng dòng dữ liệu
            writer.write(String.format("%d,%s,%s,%s,%s,%s",
                    u.getId(),
                    u.getUsername(),
                    u.getFullName() != null ? u.getFullName() : "",
                    u.getPhoneNumber() != null ? u.getPhoneNumber() : "",
                    u.getEmail() != null ? u.getEmail() : "",
                    roleName));
            writer.newLine();
        }
        writer.flush();
    }
}
