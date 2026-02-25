package com.example.mysterycard.entity;

import com.example.mysterycard.enums.OrderItemStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "OrderItem")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID orderItemId;
    private int quantity;
    private double price;
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private OrderItemStatus orderItemStatus = OrderItemStatus.CONFIRMED;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    @ManyToOne
    @JoinColumn(name = "listSeller_id")
    private ListSeller listSeller;
    @ManyToMany(mappedBy = "orderItems")
    private Set<Shipment> shipments = new HashSet<>();
}
