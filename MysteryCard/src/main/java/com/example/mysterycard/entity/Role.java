package com.example.mysterycard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

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
    @ManyToMany
    @JoinTable(
            name = "role_Privilege",
            joinColumns = @JoinColumn(name = "role_code"),
            inverseJoinColumns = @JoinColumn(name = "privilege_code")
    )
    private Set<Privilege> privileges = new HashSet<>();

    @ManyToMany(mappedBy = "rolelist")
    private Set<Users> users = new HashSet<>();

}
