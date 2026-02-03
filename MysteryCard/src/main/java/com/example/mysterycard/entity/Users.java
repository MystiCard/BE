package com.example.mysterycard.entity;

import com.example.mysterycard.enums.Gender;
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
    private String phone;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private int tokenVersion = 0;
    private String avatarUrl;
    private String address;
    private String districtId;
    private String wardId;
    @Builder.Default
    private boolean active = true;
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
    private List<Feedback> reviewerList = new ArrayList<>();
    @OneToMany(mappedBy = "seller")
    private List<Feedback>  revieweeList= new ArrayList<>();
    @OneToOne(mappedBy = "onwer")
    private Wallet wallet;
    @OneToMany(mappedBy = "user")
    private List<BankAccount> bankAccountslist = new ArrayList<>();
    @OneToMany(mappedBy = "owner")
    private List<BlindBoxResult> blindBoxResults = new ArrayList<>();
    @OneToMany(mappedBy = "user")
    private List<WishList> wishLists = new ArrayList<>();
    @OneToMany(mappedBy = "shipper")
    private List<Shipment> shipmentList = new ArrayList<>();
}
