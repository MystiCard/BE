package com.example.mysterycard.entity;

import com.example.mysterycard.enums.Rarity;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "Card")
@NoArgsConstructor
@Getter
@Setter
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID cardId;
    private String name;
    @Enumerated(EnumType.STRING)
    private Rarity rarity;
    @ManyToOne
    @JoinColumn(name="category_id")
    private Category category;
    @OneToMany(mappedBy = "card")
    private List<ListSeller> listSellers = new ArrayList<>();
    @OneToMany(mappedBy = "card")
    private List<BlindBoxCard> blindBoxCards = new ArrayList<>();
    @OneToMany(mappedBy = "card")
    private List<BlindBoxResult> blindBoxResults = new ArrayList<>();
    @OneToMany(mappedBy = "card",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();
    private double basePrice;
    private double minPrice;
    private double maxPrice;

}
