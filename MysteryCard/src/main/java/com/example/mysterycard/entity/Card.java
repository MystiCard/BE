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
    private String imageUrl;
    @ManyToOne
    @JoinColumn(name="category_id")
    private Category category;
    @OneToMany(mappedBy = "card")
    private List<ListSeller> listSellers = new ArrayList<>();
    @ManyToMany(mappedBy = "cardList")
    private Set<BlindBox> blindBoxes = new HashSet<>();
    @OneToOne(mappedBy = "card")
    private BlindBoxResult blindBoxResult;
    @OneToMany(mappedBy = "card",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

}
