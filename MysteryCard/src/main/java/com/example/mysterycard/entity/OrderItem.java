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
    private OrderItemStatus orderItemStatus = OrderItemStatus.PENDING_CONFIRM;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    @ManyToOne
    @JoinColumn(name = "listSeller_id")
    private ListSeller listSeller;
    @ManyToMany(mappedBy = "orderItems")
    @OrderBy("createAt ASC")
    private Set<Shipment> shipments = new HashSet<>();
    @ManyToOne
    @JoinColumn(name = "return_request_id")
    private ReturnRequest returnRequest;
}
