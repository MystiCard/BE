package com.example.mysterycard.entity;

import com.example.mysterycard.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "Orders")
@NoArgsConstructor
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID orderId;
    private Long totalAmount;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private LocalDateTime orderDate;
    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private Users buyer;
    @OneToOne
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;
    @OneToMany(mappedBy = "order")
    private List<WalletTransaction> transactionList = new ArrayList<>();
    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItemList = new ArrayList<>();
}
