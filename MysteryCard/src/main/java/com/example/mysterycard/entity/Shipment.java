package com.example.mysterycard.entity;

import com.example.mysterycard.enums.ShippingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "Shipment")
@NoArgsConstructor
@Getter
@Setter
public class Shipment {
@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID shipmentId;
    private String shipmentAddress;
    @Enumerated(EnumType.STRING)
    private ShippingStatus shipmentStatus;
    private String trackingCode;
    private String shipperName;
    private String shipperContact;
    private String provider;
    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;


}
