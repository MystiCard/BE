package com.example.mysterycard.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "Roles")
public class Role {
    @Id
    private String roleCode;
    private String roleName;
    private String description;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @Builder.Default
    private boolean active = true;
    @ManyToMany
    @JoinTable(
            name = "role_Privilege",
            joinColumns = @JoinColumn(name = "role_code"),
            inverseJoinColumns = @JoinColumn(name = "privilege_code")
    )
    private Set<Permision> permisions = new HashSet<>();

    @ManyToMany(mappedBy = "rolelist")
    private Set<Users> users = new HashSet<>();

}
