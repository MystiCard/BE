package com.example.mysterycard.entity;

import com.example.mysterycard.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

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
    private OrderStatus status= OrderStatus.CREATED;
    @CreationTimestamp
    private LocalDateTime orderDate;
    private int quantity;
    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private Users buyer;
    @OneToMany(mappedBy = "order")
    private List<Shipment> shipment = new ArrayList<>();
    @OneToMany(mappedBy = "order")
    private List<WalletTransaction> transactionList = new ArrayList<>();
    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItemList = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "blind_box_id")
    private BlindBox blindBox;
    @OneToMany(mappedBy = "order")
    private List<BlindBoxResult> blindBoxResults = new ArrayList<>();
}
