package com.topcit.aims.aims.infrastructure.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "permissions")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "code_permission", nullable = false, unique = true)
    private String codePermission;
    @Column(name = "descriptions", nullable = false)
    private String description;
    @Column(name = "module", nullable = false)
    private String module;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodePermission() {
        return codePermission;
    }

    public void setCodePermission(String codePermission) {
        this.codePermission = codePermission;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }
}
