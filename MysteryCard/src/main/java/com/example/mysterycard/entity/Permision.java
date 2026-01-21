package com.example.mysterycard.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
@Builder
@Entity
@Table(name = "Permision")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Permision {
    @Id
    private String permisionCode;
    private String permisionName;
    private String description;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public Permision(String permisionCode, String permisionName, String description) {
        this.permisionCode = permisionCode;
        this.permisionName = permisionName;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }
    @ManyToMany(mappedBy = "permisions")
    private Set<Role> roles = new HashSet<>();

    @Override
    public String toString() {
        return "Permision{" +
                "createdAt=" + createdAt +
                ", description='" + description + '\'' +
                ", permisionName='" + permisionName + '\'' +
                ", permisionCode='" + permisionCode + '\'' +
                '}';
    }
}
