package com.example.mysterycard.entity;

import com.example.mysterycard.enums.ShippingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "Shipment")
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID shipmentId;
    private String buyerAddress;
    @Builder.Default
    private LocalDateTime createAt = LocalDateTime.now();
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private ShippingStatus shipmentStatus = ShippingStatus.PENDING;
    private Long shipmentFee;
    private Long fromDistrictId;
    private Long toDistrictId;
    private Long toWardId;
    private String buyerPhone;
    private String sellerPhone;
    private String sellerAddress;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    @ManyToOne
    @JoinColumn(name = "shipper_id")
    private Users shipper;
    @OneToMany(mappedBy = "shipment")
    private List<Tracking> trackingList = new ArrayList<>();

}
