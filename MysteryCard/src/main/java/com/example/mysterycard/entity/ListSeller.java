package com.example.mysterycard.entity;

import com.example.mysterycard.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "ListSeller")
@NoArgsConstructor
@Getter
@Setter
public class ListSeller {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID listSellerId;
    private double price;
    private int quantity;
    private Status status;
    private LocalDateTime createdAt;
    @ManyToOne
    @JoinColumn(name = "card_id")
    private Card card;
    @ManyToOne
    @JoinColumn(name = "seller_id")
    private Users seller;
    @OneToMany(mappedBy = "listSeller")
    private List<OrderItem> orderItem;

}
