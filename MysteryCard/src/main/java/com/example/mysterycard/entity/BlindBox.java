package com.example.mysterycard.entity;

import com.example.mysterycard.enums.BlindBoxStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "BlindBox")
@NoArgsConstructor
@Getter
@Setter
public class BlindBox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long blindBoxId;
    private String name;
    private String description;
    private String imageUrl;
    private double drawPrice;
    private double allBoxPrice;
    @Enumerated(EnumType.STRING)
    private BlindBoxStatus blindBoxStatus = BlindBoxStatus.ACTIVE;
    @OneToMany(mappedBy = "blindBox", cascade = CascadeType.ALL , orphanRemoval = true)
    private List<BlindBoxCard> blindBoxCards = new ArrayList<>();
    @ManyToMany(mappedBy = "blindBoxesList",cascade = CascadeType.ALL)
    private List<RateConfig> rateConfigList = new ArrayList<>();
    @OneToMany(mappedBy = "blindBox")
    private List<BlindBoxPurChase> blindBoxPurChaseList = new ArrayList<>();
    @OneToMany(mappedBy = "blindBox")
    private List<Feedback> blindBoxOpenList = new ArrayList<>();
}
