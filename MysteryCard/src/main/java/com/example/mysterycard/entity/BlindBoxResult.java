package com.example.mysterycard.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "BlindBoxResult")
@NoArgsConstructor
@Getter
@Setter
public class BlindBoxResult {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID blindBoxResultId;
    private int quantity;
    @ManyToOne
    @JoinColumn(name = "blind_box_purchase_id")
    private BlindBoxPurChase blindBoxPurchase;
    @OneToOne
    @JoinColumn(name = "card_id")
    private Card card;
}
