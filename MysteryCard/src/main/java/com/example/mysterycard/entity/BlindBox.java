package com.example.mysterycard.entity;

import com.example.mysterycard.enums.BlindBoxStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "BlindBox")
@NoArgsConstructor
@Getter
@Setter
public class BlindBox {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID blindBoxId;
    private String name;
    private String description;
    private String imageUrl;
    private Long drawPrice;
    private Long allBoxPrice;
    @Enumerated(EnumType.STRING)
    private BlindBoxStatus blindBoxStatus = BlindBoxStatus.ACTIVE;
    @OneToMany(mappedBy = "blindBox", cascade = CascadeType.ALL , orphanRemoval = true)
    private List<BlindBoxCard> blindBoxCards = new ArrayList<>();
    @ManyToMany(mappedBy = "blindBoxesList",cascade = CascadeType.ALL)
    private List<RateConfig> rateConfigList = new ArrayList<>();
    @OneToMany(mappedBy = "blindBox")
    private List<Order> orderListd = new ArrayList<>();
    @OneToMany(mappedBy = "blindBox")
    private List<Feedback> blindBoxOpenList = new ArrayList<>();
}
