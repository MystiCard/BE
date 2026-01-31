package com.example.mysterycard.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@Entity
@Table(name = "wishlist")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class WishList {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID wishList;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private  Users user;
    @ManyToOne
    @JoinColumn(name = "card_id")
    private  Card card;
    private Long expectPrice;
}
