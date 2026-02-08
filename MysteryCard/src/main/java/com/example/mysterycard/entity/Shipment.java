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
    @ManyToMany
    @JoinTable(
            name = "orderDetail_Shipment",
            joinColumns = @JoinColumn(name = "shipment_id"),
            inverseJoinColumns = @JoinColumn(name = "order_detail_id")

    )
    private Set<OrderItem> orderItems = new HashSet<>();
    @ManyToMany
    @JoinTable(
            name = "blind_box_result_shipment",
            joinColumns = @JoinColumn(name = "shipment_id"),
            inverseJoinColumns = @JoinColumn(name = "blind_box_id")

    )
    private Set<BlindBoxResult> blindBoxResults = new HashSet<>();
    @ManyToOne
    @JoinColumn(name = "shipper_id")
    private Users shipper;
    @OneToMany(mappedBy = "shipment")
    private List<Tracking> trackingList = new ArrayList<>();

}
