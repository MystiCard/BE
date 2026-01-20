package com.example.mysterycard.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "Users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;
    private String email;
    private String password;
    private String name;
    private int tokenVersion = 0;
    private String avatarUrl;
    private String address;
    @Builder.Default
    private boolean isActive = true;
    @Builder.Default
    private LocalDateTime createAt = LocalDateTime.now();
    @ManyToMany
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> rolelist = new HashSet<>();
    @OneToMany(mappedBy = "seller")
    private List<ListSeller> listSellers = new ArrayList<>();

    @OneToMany(mappedBy = "buyer")
    private List<Order> orderList = new ArrayList<>();
    @OneToMany(mappedBy = "buyer")
    private List<BlindBoxPurChase> blindBoxPurChaseList = new ArrayList<>();
    @OneToMany(mappedBy = "buyer")
    private List<Feedback> reviewerList = new ArrayList<>();
    @OneToMany(mappedBy = "seller")
    private List<Feedback>  revieweeList= new ArrayList<>();
}
