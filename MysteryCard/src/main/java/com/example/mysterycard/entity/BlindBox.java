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
    private double price;
    @Enumerated(EnumType.STRING)
    private BlindBoxStatus blindBoxStatus;
    private int quantity;
    @ManyToMany
    @JoinTable(
            name = "BlindBox_Card",
            joinColumns = @JoinColumn(name = "blind_box_id"),
            inverseJoinColumns = @JoinColumn(name = "card_id")
    )
    private Set<Card> cardList = new HashSet<>();
    @ManyToMany(mappedBy = "blindBoxesList")
    private Set<RateConfig> rateConfigList = new HashSet<>();
    @OneToMany(mappedBy = "blindBox")
    private List<BlindBoxPurChase> blindBoxPurChaseList = new ArrayList<>();
    @OneToMany(mappedBy = "blindBox")
    private List<Feedback> blindBoxOpenList = new ArrayList<>();
}
