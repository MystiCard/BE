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
    private String toAddress;
    @Builder.Default
    private LocalDateTime createAt = LocalDateTime.now();
    @Enumerated(EnumType.STRING)
    private ShippingStatus shipmentStatus ;
    private Long shipmentFee;
    private Long fromDistrictId;
    private Long toDistrictId;
    private Long toWardId;
    private String toPhone;
    private String fromPhone;
    private String fromAddress;
    @ManyToMany
    @JoinTable(
            name = "orderDetail_Shipment",
            joinColumns = @JoinColumn(name = "shipment_id"),
            inverseJoinColumns = @JoinColumn(name = "order_detail_id")

    )
    @Builder.Default
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
    @OrderBy("createAt ASC")
    private List<Tracking> trackingList = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

}
