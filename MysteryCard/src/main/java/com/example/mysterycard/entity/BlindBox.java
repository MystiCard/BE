package com.example.mysterycard.entity;

import com.example.mysterycard.enums.BlindBoxStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "BlindBox")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class BlindBox {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID blindBoxId;
    private String name;
    private String description;
    private String imageUrl;
    private double drawPrice;
    private Long allBoxPrice;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private BlindBoxStatus blindBoxStatus = BlindBoxStatus.ACTIVE;
    @OneToMany(mappedBy = "blindBox", cascade = CascadeType.ALL , orphanRemoval = true)
    private List<BlindBoxCard> blindBoxCards = new ArrayList<>();
    @ManyToMany(mappedBy = "blindBoxesList",cascade = CascadeType.ALL)
    private List<RateConfig> rateConfigList = new ArrayList<>();
//    @OneToMany(mappedBy = "blindBox")
//    private List<Order> orderListd = new ArrayList<>();
//    @OneToMany(mappedBy = "blindBox")
//    private List<Feedback> blindBoxOpenList = new ArrayList<>();
}
