package com.example.mysterycard.entity;

import jakarta.persistence.*;
import lombok.*;
import org.apache.catalina.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Cart")
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID cartId;
    private int quantity;
    private double price;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;
    @OneToOne
    @JoinColumn(name = "listseller_id")
    private ListSeller listSeller ;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
