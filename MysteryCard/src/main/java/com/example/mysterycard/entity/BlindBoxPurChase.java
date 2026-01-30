package com.example.mysterycard.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "BlindBoxPurChase")
@NoArgsConstructor
@Getter
@Setter
public class BlindBoxPurChase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID blindBoxPurchaseId;
    private double price;
    private boolean isOpened = false;
    @CreationTimestamp
    private LocalDateTime purchaseDate;
    @ManyToOne
    @JoinColumn(name ="buyer_id")
    private Users buyer;
    @ManyToOne
    @JoinColumn(name = "blind_box_id")
    private BlindBox blindBox;
    @OneToMany(mappedBy = "blindBoxPurchase")
    private List<BlindBoxResult> blindBoxResultsList= new ArrayList<>();
    @OneToMany(mappedBy = "blindboxpurchase")
    private List<WalletTransaction> transactionList = new ArrayList<>();
    @OneToOne
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;
}
