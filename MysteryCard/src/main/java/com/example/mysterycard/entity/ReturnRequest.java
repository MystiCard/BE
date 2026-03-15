package com.example.mysterycard.entity;

import com.example.mysterycard.enums.ReturnRequestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Entity
@Table(name = "Return_Request")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReturnRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID returnRequestId;
    private String reason;
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private ReturnRequestStatus status = ReturnRequestStatus.REQUESTED;
    @Builder.Default
    @OneToMany(mappedBy = "returnRequest")
    private List<OrderItem> orderItemList = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private Users buyer;
    @Builder.Default
    @OneToMany(mappedBy = "returnRequest")
    private List<Image> images = new ArrayList<>();
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @OneToOne
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;

}
