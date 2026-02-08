package com.topcit.aims.aims.domain.model;

import java.util.List;
import com.topcit.aims.aims.domain.model.Role;

public class User {
    private Long id;
    private String username;
    private String password;
//    private List<String> roles;
//
//    public boolean hasRole(String role) {
//        return roles.contains(role);
//    }
    private Role role;

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public User(Long id, String username, String password, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }
    public User(){

    }
}
