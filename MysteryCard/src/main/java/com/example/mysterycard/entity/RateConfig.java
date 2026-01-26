package com.example.mysterycard.entity;

import com.example.mysterycard.enums.Rarity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "RateConfig")
@NoArgsConstructor
@Getter
@Setter
public class RateConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID rateConfigId;
    @Enumerated(EnumType.STRING)
    private Rarity cardRarity;
    private double dropRate;
  @ManyToMany
    @JoinTable(name = "rate_config_blind_box",
            joinColumns = @JoinColumn(name = "rate_config_id"),
            inverseJoinColumns = @JoinColumn(name = "blind_box_id")
    )
    private Set<BlindBox> blindBoxesList = new HashSet<>();
}
