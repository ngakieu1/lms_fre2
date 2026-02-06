package com.topcit.aims.aims.domain.model;

import java.util.List;

public class User {
    private Long id;
    private String username;
    private String password;
    private List<String> roles;

    public boolean hasRole(String role) {
        return roles.contains(role);
    }
}
