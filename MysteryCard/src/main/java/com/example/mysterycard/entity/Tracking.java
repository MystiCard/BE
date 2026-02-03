package com.example.mysterycard.entity;

import com.example.mysterycard.enums.ShippingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Shipment")
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Tracking {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID trackingId;
    @Builder.Default
    private LocalDateTime createAt = LocalDateTime.now();
    @Enumerated(EnumType.STRING)
    private ShippingStatus shippingStatus;
    @OneToMany(mappedBy = "tracking")
    private List<Image> images = new ArrayList<>();
    private String note;
    @ManyToOne
    @JoinColumn(name = "shipments_id")
    private Shipment shipment;


}
